package hu.ridesharing.service;

import hu.ridesharing.dto.DriverDTO;
import hu.ridesharing.dto.request.RideFilterRequest;
import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.entity.*;
import hu.ridesharing.exception.JoinRideException;
import hu.ridesharing.repository.JourneyPassengerRepository;
import hu.ridesharing.repository.JourneyRepository;
import hu.ridesharing.repository.PassengerRepository;
import hu.ridesharing.repository.RatingRepository;
import hu.ridesharing.repository.specification.JourneySpecificationFactory;
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
import java.util.UUID;

@Service
@Slf4j
public class JourneyService {

    private final JourneyRepository journeyRepository;

    private final PassengerRepository passengerRepository;

    private final RatingRepository ratingRepository;

    private final JourneyPassengerRepository journeyPassengerRepository;

    private final EmailService emailService;

    @Autowired
    public JourneyService(JourneyRepository journeyRepository, PassengerRepository passengerRepository,
                          RatingRepository ratingRepository, EmailService emailService,
                          JourneyPassengerRepository journeyPassengerRepository) {

        this.journeyRepository = journeyRepository;
        this.passengerRepository = passengerRepository;
        this.ratingRepository = ratingRepository;
        this.emailService = emailService;
        this.journeyPassengerRepository = journeyPassengerRepository;
    }

    public Journey addDrive(Journey drive) {
        log.debug("Adding drive: {}", drive);
        return this.journeyRepository.save(drive);
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
        var sort = Sort.by(filterRequest.getSortBy());
        var pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getPageSize(),
                filterRequest.getDirection().equalsIgnoreCase("desc") ? sort.descending() : sort);

        log.debug("Advanced filtering rides with filter request: {}", filterRequest);

        Specification<Journey> spec = JourneySpecificationFactory.findByFromCity(filterRequest.getPickupFrom())
                .and(JourneySpecificationFactory.findByToCity(filterRequest.getDropOffTo()))
                .and(JourneySpecificationFactory.findByDate(filterRequest.getDateFrom(), filterRequest.getDateTo()))
                .and(JourneySpecificationFactory.findBySeats(filterRequest.getSeats()))
                .and(JourneySpecificationFactory.findByMaxPrice(filterRequest.getMaxPrice()))
                .and(JourneySpecificationFactory.findByRating(
                        filterRequest.getRating(), filterRequest.isShowWithoutRating())
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
        Passenger passenger = new Passenger();
        passenger.setUsername(username);

        var count = journeyPassengerRepository.findJourneyPassengersByPassenger(passenger).size();
        log.debug("Found {} rides for {}", count, username);
        return count;
    }

    public int getDriveCountByUsername(String username) {
        Driver driver = new Driver();
        driver.setUsername(username);
        var count = journeyRepository.findByDriver(driver).size();
        log.debug("Found {} drives for {}", count, username);
        return count;
    }

    @Transactional
    public void joinRide(Long id, String passengerUsername, String passengerEmail, String passengerFullName) {
        Journey journey = journeyRepository.findById(id).orElseThrow();

        var passenger = passengerRepository.findById(passengerUsername);
        Passenger savedPassenger;
        if (passenger.isEmpty()) {
            Passenger newPassenger = new Passenger();
            newPassenger.setUsername(passengerUsername);
            newPassenger.setFullName(passengerFullName);
            newPassenger.setEmailAddress(passengerEmail);

            savedPassenger = passengerRepository.save(newPassenger);
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
            throw new JoinRideException(e.getMessage(), e);
        }

        try {
            emailService.sendRideAcceptEmail(journey, savedPassenger, secureToken);
        } catch (MessagingException e) {
            //TODO
            e.printStackTrace();
        }
    }

    private JourneyResponseDTO mapToResponse(Journey journey) {
        JourneyResponseDTO response = new JourneyResponseDTO();
        response.setId(journey.getId());
        response.setSeats(journey.getSeats());
        response.setPrice(journey.getPassengerPrice());
        DriverDTO driver = new DriverDTO();
        driver.setUsername(journey.getDriver().getUsername());
        driver.setFullName(journey.getDriver().getFullName());
        driver.setRating(ratingRepository.findByDriver(journey.getDriver()).stream()
                .mapToDouble(Rating::getValue)
                .average()
                .orElse(0)
        );
        response.setDriver(driver);
        response.setFromCity(journey.getFromCity());
        response.setToCity(journey.getToCity());
        response.setArrivalTime(journey.getArrive());
        response.setDepartureTime(journey.getDepart());
        response.setCarMake(journey.getCarMake());
        return response;
    }
}
