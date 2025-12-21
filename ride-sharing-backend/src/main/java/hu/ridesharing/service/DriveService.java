package hu.ridesharing.service;

import hu.ridesharing.entity.Drive;
import hu.ridesharing.repository.DriveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DriveService {

    private final DriveRepository driveRepository;

    @Autowired
    public DriveService(DriveRepository driveRepository) {
        this.driveRepository = driveRepository;
    }

    public Drive addDrive(Drive drive) {
        return this.driveRepository.save(drive);
    }
}
