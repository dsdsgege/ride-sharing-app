package hu.ridesharing.repository;

import hu.ridesharing.entity.JourneyPassenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyPassengerRepository extends JpaRepository<JourneyPassenger, String> {
}
