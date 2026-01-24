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
        log.debug("Adding driver: {}", driver.getFullName());
        if (!driverRepository.existsById(driver.getFullName())) {
            driverRepository.save(driver);
        }
    }

    public double getDriverRatingByFullName(String fullName) {
        return driverRepository.findById(fullName).orElseThrow(
                () -> new DriverNotFoundException("No driver was found with the name " + fullName)
        ).getRating();
    }
}
