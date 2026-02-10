package hu.ridesharing.repository;

import hu.ridesharing.entity.JourneyPassenger;
import hu.ridesharing.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JourneyPassengerRepository extends JpaRepository<JourneyPassenger, String> {

    List<JourneyPassenger> findJourneyPassengersByPassenger(Passenger passenger);
}
