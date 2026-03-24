package hu.ridesharing.service;

import hu.ridesharing.dto.DriverDTO;
import hu.ridesharing.dto.request.RideFilterRequest;
import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.dto.response.outgoing.JourneyResponseWithPassengersDTO;
import hu.ridesharing.dto.response.outgoing.ResponseStatus;
import hu.ridesharing.entity.*;
import hu.ridesharing.exception.*;
import hu.ridesharing.repository.JourneyPassengerRepository;
import hu.ridesharing.repository.JourneyRepository;
import hu.ridesharing.repository.RatingRepository;
import hu.ridesharing.repository.UserRepository;
import hu.ridesharing.repository.specification.JourneySpecificationFactory;
import hu.ridesharing.exception.EmailSendingError;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    private final String adminEmail;

    private static final String ADMIN_TEXT =
                    """
                    Dear admin,
                
                    We could not send email to some of the passengers/driver of the ride %d.
                
                    Error: %s
                    """;

    @Autowired
    public JourneyService(JourneyRepository journeyRepository, RatingRepository ratingRepository,
                          EmailService emailService, JourneyPassengerRepository journeyPassengerRepository,
                          UserRepository userRepository, @Value("${app.admin.email}") String adminEmail) {

        this.journeyRepository = journeyRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.emailService = emailService;
        this.journeyPassengerRepository = journeyPassengerRepository;
        this.adminEmail = adminEmail;
    }

    public Journey addDrive(Journey drive, String username) {
        if (!username.equals(drive.getDriver().getUsername())) {
            throw new ForbiddenException("You cant share ride if you are not the driver.");
        }

        log.debug("Adding drive: {}", drive);
        return this.journeyRepository.save(drive);
    }

    public Page<JourneyResponseWithPassengersDTO> getMyDrives(String username, int page) {
        User driver = new User();
        driver.setUsername(username);

        Page<Journey> drives = journeyRepository.findByDriver(driver, PageRequest.of(0, (page + 1) * 10));
        log.debug("Fetching drives for driver {}", username);

        return drives.map(journey -> {
            List<User> passengers = journeyPassengerRepository.findAcceptedPassengersByJourney(journey);
            var passengersFiltered = passengers.stream()
                    .filter(
                            passenger -> ratingRepository.findPassengersByJourney(journey)
                                    .stream()
                                    .noneMatch(passengerRated -> passengerRated.equals(passenger))
                    )
                    .toList();

            log.debug("Found {} passengers for drive {}", passengersFiltered.size(), journey.getId());
            return mapToExtendedResponse(journey, passengersFiltered);
        });
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

    public Page<JourneyResponseDTO> getMyRides(int page, String username) {
        User passenger = new User();
        passenger.setUsername(username);

        return journeyPassengerRepository.findAcceptedJourneyByPassenger(
                passenger, PageRequest.of(0, (page + 1) * 10)
        ).map(this::mapToResponse);

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

    public long getRideCountByUsername(String username) {
        User passenger = new User();
        passenger.setUsername(username);

        var count = journeyPassengerRepository.countByPassengerAccepted(passenger);
        log.debug("Found {} rides for {}", count, username);
        return count;
    }

    public long getDriveCountByUsername(String username) {
        User driver = new User();
        driver.setUsername(username);
        var count = journeyRepository.countByDriver(driver);
        log.debug("Found {} drives for {}", count, username);
        return count;
    }

    @Transactional
    public ResponseStatus joinRide(Long id, String passengerUsername, String passengerEmail, String passengerFullName) {
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

        if (journeyPassengerRepository.existsByJourneyAndPassenger(journey, savedPassenger)) {
            throw new JoinRideException("You have already joined this ride.");
        }

        // to save the relationship (accepted is false by default),
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
            // Here we need to let the user know that the join was unsuccessful!
            throw new EmailSendingError("Could not send approve email for driver.");
        }

        return new ResponseStatus(true);
    }

    @Transactional
    public boolean deleteDrive(Long id, String username) {
        Journey journey = checkMyJourney(username, id);

        if (journey.getDepart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("You can not delete this drive.");
        }

        journeyRepository.deleteById(id);

        journeyPassengerRepository.findAcceptedPassengersByJourney(journey).forEach(passenger -> {
            try {
                emailService.sendRideHasBeenCanceledEmail(journey, passenger);
            } catch (MessagingException e) {
                log.error("Could not send cancellation email for passenger {}, journey id: {}",
                        passenger.getUsername(), journey.getId());
                emailService.sendAdminEmail(journey, e, adminEmail, ADMIN_TEXT,
                        "Could not send cancellation email for passenger");
            }
        });

        return true;
    }

    @Transactional
    public boolean cancelRide(Long id, String username) {
        Journey journey = journeyRepository.findById(id).orElseThrow();

        if (journey.getDepart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("You can not cancel this ride as it is in the past.");
        }

        User passenger = new User();
        passenger.setUsername(username);

        if (!journeyPassengerRepository.existsByJourneyAndPassenger(journey, passenger)) {
            throw new ForbiddenException("No such ride found for user " + username);
        }

        journeyPassengerRepository.deleteJourneyPassengersByJourneyAndPassenger(journey, passenger);

        try {
            emailService.sendPassengerLeftEmail(journey, passenger,
                    journeyPassengerRepository.findAcceptedPassengersByJourney(journey).size()
            );
        } catch (MessagingException e) {
            log.error("Could not send cancellation email for driver, journey id: {}", journey.getId());
            emailService.sendAdminEmail(journey, e, adminEmail, ADMIN_TEXT,
                    "Could not send cancellation email for driver");
        }

        return true;
    }

    public boolean updateDrive(Journey drive, String username) {
        Journey journey = checkMyJourney(username, drive.getId());

        if (StringUtils.isBlank(drive.getCarMake()) || drive.getArrive() == null || drive.getDepart() == null) {
            log.warn("Some of the fields are empty drive: {}", drive);
            throw new BadRequestException("You must fill every input");
        }

        String oldDepart = journey.getDepart().toString();

        journey.setCarMake(drive.getCarMake());
        journey.setSeats(drive.getSeats());
        journey.setArrive(drive.getArrive());
        journey.setDepart(drive.getDepart());

        var savedJourney = journeyRepository.save(journey);

        journeyPassengerRepository.findAcceptedPassengersByJourney(journey).forEach(passenger -> {
            try {
                emailService.sendRideHasChangedEmail(journey, passenger, oldDepart);
            } catch (MessagingException e) {
                log.error("Could not send cancellation email for passenger: {}, journey id: {}",
                        passenger.getUsername(), journey.getId());
                this.emailService.sendAdminEmail(journey, e, adminEmail, ADMIN_TEXT,
                        "Could not send update email for passenger");
            }
        });

        return savedJourney.getArrive() == journey.getArrive() &&
                savedJourney.getDepart() == journey.getDepart() &&
                savedJourney.getCarMake().equals(journey.getCarMake()) &&
                savedJourney.getSeats() == journey.getSeats();
    }

    /**
     * Checks if the drive is stored and the user from jwt is the driver of the drive.
     *
     * @param username user of the jwt
     * @param id journey id
     * @return found Journey
     */
    public Journey checkMyJourney(String username, Long id) {
        Optional<Journey> journey = journeyRepository.findById(id);
        if (journey.isEmpty()) {
            throw new DriveNotFoundException("No such drive found for user " + username);
        }

        if (!journey.get().getDriver().getUsername().equals(username)) {
            throw new ForbiddenException("You are not the driver of this drive.");
        }

        return journey.get();
    }

    private JourneyResponseDTO mapToResponse(Journey journey) {
        JourneyResponseDTO response = new JourneyResponseDTO();
        setBasicForResponse(journey, response);
        return response;
    }

    private JourneyResponseWithPassengersDTO mapToExtendedResponse(Journey journey, List<User> passengersToRate) {
        JourneyResponseWithPassengersDTO extendedResponse = new JourneyResponseWithPassengersDTO();
        setBasicForResponse(journey, extendedResponse);

        extendedResponse.setPassengersToRate(passengersToRate);
        return extendedResponse;
    }

    private void setBasicForResponse(Journey journey, JourneyResponseDTO response) {
        response.setId(journey.getId());
        response.setSeats(journey.getSeats());
        response.setPrice(journey.getPassengerPrice());
        DriverDTO driver = new DriverDTO();
        driver.setUsername(journey.getDriver().getUsername());
        driver.setFullName(journey.getDriver().getFullName());
        driver.setRating(ratingRepository.findByDriverRated(journey.getDriver(), Pageable.unpaged())
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
    }
}
