package hu.ridesharing.repository;

import hu.ridesharing.entity.User;
import hu.ridesharing.entity.Journey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JourneyRepository extends JpaRepository<Journey, Long>, JpaSpecificationExecutor<Journey> {
    List<Journey> findByDriver(User driver);
    Page<Journey> findByDriver(User driver, Pageable pageable);
}
