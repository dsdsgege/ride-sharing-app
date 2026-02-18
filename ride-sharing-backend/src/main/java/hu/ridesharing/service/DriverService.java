package hu.ridesharing.service;

import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.JourneyPassenger;
import hu.ridesharing.entity.Rating;
import hu.ridesharing.exception.DriverNotFoundException;
import hu.ridesharing.repository.DriverRepository;
import hu.ridesharing.repository.JourneyPassengerRepository;
import hu.ridesharing.repository.RatingRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;
    private final RatingRepository ratingRepository;

    private static final String ERROR_MESSAGE = "Driver with username %s not found!";
    private final JourneyPassengerRepository journeyPassengerRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository, RatingRepository ratingRepository, JourneyPassengerRepository journeyPassengerRepository) {
        this.driverRepository = driverRepository;
        this.ratingRepository = ratingRepository;
        this.journeyPassengerRepository = journeyPassengerRepository;
    }

    @Transactional
    public void addDriver(Driver driver) {
        log.debug("Adding driver: {}", driver.getUsername());
        if (!driverRepository.existsById(driver.getUsername())) {
            driverRepository.save(driver);
        }
    }

    public double getDriverRatingByUsername(String username) {
        return ratingRepository.findByDriver(
                driverRepository.findById(username).orElseThrow(
                        () -> new DriverNotFoundException(ERROR_MESSAGE.formatted(username))
                )
        ).stream()
                .mapToDouble(Rating::getValue)
                .average()
                .orElse(0);
    }

    @Transactional
    public boolean acceptPassenger(String token) {
        Optional<JourneyPassenger> optionalJp = journeyPassengerRepository.findById(token);
        if (optionalJp.isEmpty()) {
            return false;
        }
        JourneyPassenger jp = optionalJp.get();
        jp.setAccepted(true);

        Journey journey = jp.getJourney();
        journey.setSeats(journey.getSeats() - 1);

        return true;
    }

    public Map<String, String> findAllFullNameByUsernames(List<String> usernames) {
        return this.driverRepository.findAllFullNameByUsernames(usernames)
                .stream()
                .collect(
                        Collectors.toMap(Driver::getUsername, Driver::getFullName)
                );
    }
}
