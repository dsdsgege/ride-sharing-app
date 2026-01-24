package hu.ridesharing.service;

import hu.ridesharing.dto.DriverDTO;
import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.Passenger;
import hu.ridesharing.repository.JourneyRepository;
import hu.ridesharing.repository.specification.JourneySpecificationFactory;
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

    @Autowired
    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
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

    public int getRideCountByFullName(String fullName) {
        Passenger passenger = new Passenger();
        passenger.setFullName(fullName);
        var count = journeyRepository.findByPassengers(passenger).size();
        log.debug("Found {} rides for {}", count, fullName);
        return count;
    }

    public int getDriveCountByFullName(String fullName) {
        Driver driver = new Driver();
        driver.setFullName(fullName);
        var count = journeyRepository.findByDriver(driver).size();
        log.debug("Found {} drives for {}", count, fullName);
        return count;
    }

    private JourneyResponseDTO mapToResponse(Journey journey) {
        JourneyResponseDTO response = new JourneyResponseDTO();
        response.setId(journey.getId());
        response.setSeats(journey.getSeats());
        response.setPrice(journey.getPassengerPrice());
        DriverDTO driver = new DriverDTO();
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
