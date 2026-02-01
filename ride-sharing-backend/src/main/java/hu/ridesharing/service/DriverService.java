package hu.ridesharing.service;

import hu.ridesharing.entity.Driver;
import hu.ridesharing.exception.DriverNotFoundException;
import hu.ridesharing.repository.DriverRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Transactional
    public void addDriver(Driver driver) {
        log.debug("Adding driver: {}", driver.getUsername());
        if (!driverRepository.existsById(driver.getUsername())) {
            driverRepository.save(driver);
        }
    }

    public double getDriverRatingByUsername(String username) {
        return driverRepository.findById(username).orElseThrow(
                () -> new DriverNotFoundException("No driver was found with the username " + username)
        ).getRating();
    }
}
