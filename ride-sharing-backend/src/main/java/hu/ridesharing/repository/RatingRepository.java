package hu.ridesharing.repository;

import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Passenger;
import hu.ridesharing.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT r FROM Rating r WHERE r.journey.driver = :driver")
    List<Rating> findByDriver(Driver driver);

    /* native sql:
        SELECT r.*
        FROM rating r
        INNER JOIN journey j ON r.journey_id = j.id
        INNER JOIN journey_passengers jp ON j.id = jp.journey_id
        INNER JOIN passenger p ON jp.passenger_id = p.id
        WHERE p.username = ?
     */
    @Query("SELECT r FROM Rating r INNER JOIN r.journey.passengers jp WHERE jp.passenger = :passenger")
    List<Rating> findByPassenger(Passenger passenger);
}
