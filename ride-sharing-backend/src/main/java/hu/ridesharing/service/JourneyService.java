package hu.ridesharing.service;

import hu.ridesharing.dto.DriverDTO;
import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.Passenger;
import hu.ridesharing.repository.JourneyRepository;
import hu.ridesharing.repository.PassengerRepository;
import hu.ridesharing.repository.specification.JourneySpecificationFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class JourneyService {

    private final JourneyRepository journeyRepository;

    private final PassengerRepository passengerRepository;

    @Autowired
    public JourneyService(JourneyRepository journeyRepository, PassengerRepository passengerRepository) {
        this.journeyRepository = journeyRepository;
        this.passengerRepository = passengerRepository;
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

    public JourneyResponseDTO getRide(Long id) {
        Journey journey = journeyRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No ride found with id " + id)
        );
        return mapToResponse(journey);
    }

    public int getRideCountByUsername(String username) {
        Passenger passenger = new Passenger();
        passenger.setUsername(username);
        var count = journeyRepository.findByPassengers(passenger).size();
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
        journey.setSeats(journey.getSeats() - 1);

        var passenger = passengerRepository.findById(passengerUsername);
        if (passenger.isPresent()) {
            journey.getPassengers().add(passenger.get());

        } else {
            Passenger newPassenger = new Passenger();
            newPassenger.setUsername(passengerUsername);
            newPassenger.setFullName(passengerFullName);
            passengerRepository.save(newPassenger);

            journey.getPassengers().add(newPassenger);
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
        driver.setRating(journey.getDriver().getRating());
        response.setDriver(driver);
        response.setFromCity(journey.getFromCity());
        response.setToCity(journey.getToCity());
        response.setArrivalTime(journey.getArrive());
        response.setDepartureTime(journey.getDepart());
        response.setCarMake(journey.getCarMake());
        return response;
    }
}
