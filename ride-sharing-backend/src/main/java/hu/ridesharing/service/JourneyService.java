package hu.ridesharing.service;

import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.entity.Journey;
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

    private JourneyResponseDTO mapToResponse(Journey journey) {
        JourneyResponseDTO response = new JourneyResponseDTO();
        response.setSeats(journey.getSeats());
        response.setPrice(journey.getPassengerPrice());
        response.setDriver(journey.getDriver());
        response.setFromCity(journey.getFromCity());
        response.setToCity(journey.getToCity());
        response.setArrivalTime(journey.getArrive());
        response.setDepartureTime(journey.getDepart());
        response.setCarMake(journey.getCarMake());
        return response;
    }
}
