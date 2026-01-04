package hu.ridesharing.service;

import hu.ridesharing.entity.Driver;
import hu.ridesharing.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    @Autowired
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public void addDriver(Driver driver) {
        if (!driverRepository.existsById(driver.getFullName())) {
            driverRepository.save(driver);
        }
    }
}
