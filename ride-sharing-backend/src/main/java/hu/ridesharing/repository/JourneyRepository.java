package hu.ridesharing.repository;

import hu.ridesharing.entity.RatingType;
import hu.ridesharing.entity.User;
import hu.ridesharing.entity.Journey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JourneyRepository extends JpaRepository<Journey, Long>, JpaSpecificationExecutor<Journey> {
    long countByDriver(User driver);
    Page<Journey> findByDriver(User driver, Pageable pageable);

    @Query("SELECT DISTINCT j.driver FROM Journey j " +
            "INNER JOIN JourneyPassenger jp ON jp.journey = j " +
            "WHERE j NOT IN (" +
                "SELECT r.journey FROM Rating r WHERE r.type = :ratingType AND jp.passenger = r.rated" +
            ") " +
            "AND j.arrive < CURRENT_TIMESTAMP")
    List<User> findDriversEligibleForRatingEmail(@Param("ratingType") RatingType ratingType);
}
