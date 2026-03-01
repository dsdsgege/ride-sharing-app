package hu.ridesharing.service;

import hu.ridesharing.dto.DriverDTO;
import hu.ridesharing.dto.request.RideFilterRequest;
import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.entity.*;
import hu.ridesharing.exception.*;
import hu.ridesharing.repository.JourneyPassengerRepository;
import hu.ridesharing.repository.JourneyRepository;
import hu.ridesharing.repository.RatingRepository;
import hu.ridesharing.repository.UserRepository;
import hu.ridesharing.repository.specification.JourneySpecificationFactory;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class JourneyService {

    private final JourneyRepository journeyRepository;

    private final UserRepository userRepository;

    private final RatingRepository ratingRepository;

    private final JourneyPassengerRepository journeyPassengerRepository;

    private final EmailService emailService;

    @Autowired
    public JourneyService(JourneyRepository journeyRepository, RatingRepository ratingRepository,
                          EmailService emailService, JourneyPassengerRepository journeyPassengerRepository,
                          UserRepository userRepository) {

        this.journeyRepository = journeyRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.emailService = emailService;
        this.journeyPassengerRepository = journeyPassengerRepository;
    }

    public Journey addDrive(Journey drive, String username) {
        if (!username.equals(drive.getDriver().getUsername())) {
            throw new ForbiddenException("You cant share ride if you are not the driver.");
        }

        log.debug("Adding drive: {}", drive);
        return this.journeyRepository.save(drive);
    }

    public Page<JourneyResponseDTO> getMyDrives(String username, int page) {
        User driver = new User();
        driver.setUsername(username);

        log.debug("Fetching drives for driver {}", username);
        return journeyRepository.findByDriver(driver, PageRequest.of(0, (page + 1) * 10))
                .map(this::mapToResponse);
    }

    public Page<JourneyResponseDTO> getRides(String fromCity, String toCity, LocalDateTime dateFrom,
                                             LocalDateTime dateTo, Pageable pageRequest) {

        log.debug("Filtering rides from {} to {} in {}-{}", fromCity, toCity, dateFrom, dateTo);

        Specification<Journey> spec = JourneySpecificationFactory.findByFromCity(fromCity)
                .and(JourneySpecificationFactory.findByToCity(toCity))
                .and(JourneySpecificationFactory.findByDate(dateFrom, dateTo));

        Page<Journey> journeys = journeyRepository.findAll(spec, pageRequest);

        return journeys.map(this::mapToResponse);
    }

    public Page<JourneyResponseDTO> getRides(RideFilterRequest filterRequest) {
        var sort = Sort.by(filterRequest.sortBy());
        var pageable = PageRequest.of(filterRequest.page(), filterRequest.pageSize(),
                filterRequest.direction().equalsIgnoreCase("desc") ? sort.descending() : sort);

        log.debug("Advanced filtering rides with filter request: {}", filterRequest);

        Specification<Journey> spec = JourneySpecificationFactory.findByFromCity(filterRequest.pickupFrom())
                .and(JourneySpecificationFactory.findByToCity(filterRequest.dropOffTo()))
                .and(JourneySpecificationFactory.findByDate(filterRequest.dateFrom(), filterRequest.dateTo()))
                .and(JourneySpecificationFactory.findBySeats(filterRequest.seats()))
                .and(JourneySpecificationFactory.findByMaxPrice(filterRequest.maxPrice()))
                .and(JourneySpecificationFactory.findByRating(
                        filterRequest.rating(), filterRequest.showWithoutRating())
                );

        Page<Journey> journeys = journeyRepository.findAll(spec, pageable);
        return journeys.map(this::mapToResponse);
    }

    public JourneyResponseDTO getRide(Long id) {
        Journey journey = journeyRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No ride found with id " + id)
        );
        return mapToResponse(journey);
    }

    public int getRideCountByUsername(String username) {
        User passenger = new User();
        passenger.setUsername(username);

        var count = journeyPassengerRepository.findJourneyPassengersByPassenger(passenger).size();
        log.debug("Found {} rides for {}", count, username);
        return count;
    }

    public int getDriveCountByUsername(String username) {
        User driver = new User();
        driver.setUsername(username);
        var count = journeyRepository.findByDriver(driver).size();
        log.debug("Found {} drives for {}", count, username);
        return count;
    }

    /**
     * This method finds the Journeys that are eligible for rating. (Journeys that happened in the past.)
     *
     * <p>Note: It can be used to find Journeys for a simple user, but if the username is not defined, it is used to
     * find every journey that is eligible for rating.</p>
     *
     * @return Page of Journeys
     */
    public Page<JourneyResponseDTO> getEligibleForRating(User user, int page) {
        Specification<Journey> spec = JourneySpecificationFactory.findByUser(user)
                .and(JourneySpecificationFactory.findByDate(null, LocalDateTime.now()));

        return journeyRepository.findAll(spec, PageRequest.of(0, (page + 1) * 5))
                .map(this::mapToResponse);
    }

    @Transactional
    public void joinRide(Long id, String passengerUsername, String passengerEmail, String passengerFullName) {
        Journey journey = journeyRepository.findById(id).orElseThrow();

        var passenger = userRepository.findById(passengerUsername);
        User savedPassenger;
        if (passenger.isEmpty()) {
            User newPassenger = new User();
            newPassenger.setUsername(passengerUsername);
            newPassenger.setFullName(passengerFullName);
            newPassenger.setEmailAddress(passengerEmail);

            savedPassenger = userRepository.save(newPassenger);
        } else {
            savedPassenger = passenger.get();
        }

        // save the relationship (accepted is false by default)
        // the driver has to accept the Passenger through email
        String secureToken = UUID.randomUUID().toString();
        JourneyPassenger jp = new JourneyPassenger();
        jp.setJourney(journey);
        jp.setPassenger(savedPassenger);
        jp.setAcceptToken(secureToken);
        try {
            journeyPassengerRepository.save(jp);
        } catch (Exception e) {
            throw new JoinRideException("Could not join ride.");
        }

        try {
            emailService.sendRideAcceptEmail(journey, savedPassenger, secureToken);
        } catch (MessagingException e) {
            throw new EmailSendingError("Could not send approve email for driver.");
        }
    }

    public boolean deleteDrive(Long id, String username) {
        Optional<Journey> journey = journeyRepository.findById(id);
        if (journey.isEmpty()) {
            throw new DriveNotFoundException("No such drive found for user " + username);
        }

        if (!journey.get().getDriver().getUsername().equals(username)) {
            throw new ForbiddenException("You are not the driver of this drive.");
        }

        journeyRepository.deleteById(id);
        return true;
        // TODO: send emails
    }

    public boolean updateDrive(Journey drive, String username) {
        Optional<Journey> journey = journeyRepository.findById(drive.getId());
        if (journey.isEmpty()) {
            log.warn("No such drive found for user {}", username);
            throw new DriveNotFoundException("No such drive found for user " + username);
        }

        if (StringUtils.isBlank(drive.getCarMake()) || drive.getArrive() == null || drive.getDepart() == null) {
            log.warn("Some of the fields are empty drive: {}", drive);
            throw new BadRequestException("You must fill every input");
        }

        Journey updatedJourney = journey.get();
        updatedJourney.setCarMake(drive.getCarMake());
        updatedJourney.setSeats(drive.getSeats());
        updatedJourney.setArrive(drive.getArrive());
        updatedJourney.setDepart(drive.getDepart());


        if (!updatedJourney.getDriver().getUsername().equals(username)) {
            throw new ForbiddenException("You are not the driver of this drive.");
        }
        var savedJourney = journeyRepository.save(updatedJourney);
        return savedJourney.getArrive() == updatedJourney.getArrive() &&
                savedJourney.getDepart() == updatedJourney.getDepart() &&
                savedJourney.getCarMake().equals(updatedJourney.getCarMake()) &&
                savedJourney.getSeats() == updatedJourney.getSeats();
        // TODO: send emails
    }

    private JourneyResponseDTO mapToResponse(Journey journey) {
        JourneyResponseDTO response = new JourneyResponseDTO();
        response.setId(journey.getId());
        response.setSeats(journey.getSeats());
        response.setPrice(journey.getPassengerPrice());
        DriverDTO driver = new DriverDTO();
        driver.setUsername(journey.getDriver().getUsername());
        driver.setFullName(journey.getDriver().getFullName());
        driver.setRating(ratingRepository.findByDriver(journey.getDriver())
                .stream()
                .mapToDouble(Rating::getValue)
                .average()
                .orElse(0)
        );
        response.setDriver(driver);
        response.setFromCity(journey.getFromCity());
        response.setToCity(journey.getToCity());
        response.setArrive(journey.getArrive());
        response.setDepart(journey.getDepart());
        response.setCarMake(journey.getCarMake());
        return response;
    }
}
