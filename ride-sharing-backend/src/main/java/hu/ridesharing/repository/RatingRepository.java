package hu.ridesharing.repository;

import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.User;
import hu.ridesharing.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT r FROM Rating r WHERE r.rated = :driver " +
            "AND r.type = hu.ridesharing.entity.RatingType.PASSENGER_TO_DRIVER")
    List<Rating> findByDriver(User driver);

    @Query("SELECT r FROM Rating r WHERE r.rated = :passenger " +
            "AND r.type = hu.ridesharing.entity.RatingType.DRIVER_TO_PASSENGER")
    List<Rating> findByPassenger(User passenger);

    /**
     * Finds already rated passengers by a given journey.
     *
     * @param journey
     * @return
     */
    @Query("SELECT r.rated FROM Rating r WHERE r.journey = :journey " +
            "AND r.type = hu.ridesharing.entity.RatingType.DRIVER_TO_PASSENGER")
    List<User> findPassengersByJourney(Journey journey);
}
