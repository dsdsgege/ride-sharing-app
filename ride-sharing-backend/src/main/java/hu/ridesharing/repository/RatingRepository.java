package hu.ridesharing.repository;

import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.RatingId;
import hu.ridesharing.entity.User;
import hu.ridesharing.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {

    /**
     * Finds ratings given to a driver.
     *
     * @param driver
     * @param pageable
     * @return
     */
    @Query(
            value = "SELECT r FROM Rating r WHERE r.rated = :driver " +
                    "AND r.type = hu.ridesharing.entity.RatingType.PASSENGER_TO_DRIVER ORDER BY r.value",
            countQuery = "SELECT count(r) FROM Rating r WHERE r.rated = :driver " +
            "AND r.type = hu.ridesharing.entity.RatingType.PASSENGER_TO_DRIVER"
    )
    Page<Rating> findByDriverRated(User driver, Pageable pageable);

    /**
     * Finds ratings given to a passenger.
     *
     * @param passenger
     * @param pageable
     * @return
     */
    @Query(
            value ="SELECT r FROM Rating r WHERE r.rated = :passenger " +
                    "AND r.type = hu.ridesharing.entity.RatingType.DRIVER_TO_PASSENGER ORDER BY r.value",
            countQuery = "SELECT count(r) FROM Rating r WHERE r.rated = :passenger " +
                    "AND r.type = hu.ridesharing.entity.RatingType.DRIVER_TO_PASSENGER"
    )
    Page<Rating> findByPassengerRated(User passenger, Pageable pageable);

    /**
     * Finds ratings given as a driver.
     *
     * @param driver the driver who gave the rating
     * @param pageable
     * @return
     */
    @Query(
            value = "SELECT r FROM Rating r WHERE r.rater = :driver " +
                    "AND r.type = hu.ridesharing.entity.RatingType.DRIVER_TO_PASSENGER ORDER BY r.value",
            countQuery = "SELECT count(r) FROM Rating r WHERE r.rater = :driver " +
                    "AND r.type = hu.ridesharing.entity.RatingType.DRIVER_TO_PASSENGER"
    )
    Page<Rating> findByDriverRater(User driver, Pageable pageable);

    /**
     * Finds ratings given as a passenger.
     *
     * @param passenger
     * @param pageable
     * @return
     */
    @Query(
            value ="SELECT r FROM Rating r WHERE r.rater = :passenger " +
                    "AND r.type = hu.ridesharing.entity.RatingType.PASSENGER_TO_DRIVER ORDER BY r.value",
            countQuery = "SELECT count(r) FROM Rating r WHERE r.rater = :passenger " +
                    "AND r.type = hu.ridesharing.entity.RatingType.PASSENGER_TO_DRIVER"
    )
    Page<Rating> findByPassengerRater(User passenger, Pageable pageable);


    /**
     * Finds already rated passengers by a given journey.
     *
     * @param journey
     * @return
     */
    @Query("SELECT r.rated FROM Rating r WHERE r.journey = :journey " +
            "AND r.type = hu.ridesharing.entity.RatingType.DRIVER_TO_PASSENGER")
    List<User> findPassengersByJourney(Journey journey);

    long countByRated(User rated);

    long countByRater(User rater);
}
