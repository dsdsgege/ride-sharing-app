package hu.ridesharing.repository;

import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.JourneyPassenger;
import hu.ridesharing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JourneyPassengerRepository extends JpaRepository<JourneyPassenger, String> {

    List<JourneyPassenger> findJourneyPassengersByPassenger(User passenger);

    boolean existsByJourneyAndPassenger(Journey journey, User passenger);

    @Query("SELECT jp.passenger FROM JourneyPassenger jp WHERE jp.journey = :journey AND jp.accepted = true")
    List<User> findAcceptedPassengersByJourney(Journey journey);
}
