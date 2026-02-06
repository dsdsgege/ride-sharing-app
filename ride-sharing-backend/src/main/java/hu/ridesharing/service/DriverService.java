package hu.ridesharing.service;

import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Rating;
import hu.ridesharing.exception.DriverNotFoundException;
import hu.ridesharing.repository.DriverRepository;
import hu.ridesharing.repository.RatingRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;
    private final RatingRepository ratingRepository;

    private static final String ERROR_MESSAGE = "Driver with username %s not found!";

    @Autowired
    public DriverService(DriverRepository driverRepository, RatingRepository ratingRepository) {
        this.driverRepository = driverRepository;
        this.ratingRepository = ratingRepository;
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
}
