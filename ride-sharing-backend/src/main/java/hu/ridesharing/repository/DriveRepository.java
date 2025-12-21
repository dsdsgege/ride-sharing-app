package hu.ridesharing.repository;

import hu.ridesharing.entity.Drive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriveRepository extends JpaRepository<Drive, Long> {
}
