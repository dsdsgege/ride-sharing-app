package hu.ridesharing.repository.specification;

import hu.ridesharing.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class JourneySpecificationFactory {

    private JourneySpecificationFactory() {
    }

    public static Specification<Journey> findByFromCity(String fromCity) {
        return (root, query, cb) -> cb.equal(root.get(Journey_.FROM_CITY), fromCity);
    }

    public static Specification<Journey> findByToCity(String toCity) {
        return (root, query, cb) -> cb.equal(root.get(Journey_.TO_CITY), toCity);
    }

    public static Specification<Journey> findByDate(LocalDateTime dateFrom, LocalDateTime dateTo) {
        if (dateFrom == null && dateTo == null) {
            return null;
        }

        if (dateFrom == null) {
            return (root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get(Journey_.ARRIVE), dateTo);
        }

        if (dateTo == null) {
            return (root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get(Journey_.ARRIVE), dateFrom);
        }

        return (root, query, cb) -> cb.between(root.get(Journey_.DEPART), dateFrom, dateTo);
    }

    public static Specification<Journey> findBySeats(int seats) {
        if (seats == 0 ) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Journey_.SEATS), seats);
    }

    public static Specification<Journey> findByMaxPrice(double maxPrice) {
        if (maxPrice == 0) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Journey_.PASSENGER_PRICE), maxPrice);
    }

    public static Specification<Journey> findByRating(double rating, boolean showWithoutRating) {

        return (root, query, cb) -> {
            // create subquery
            Subquery<Double> subquery = query.subquery(Double.class);
            Root<Rating> ratingRoot = subquery.from(Rating.class);

            // join Rating to Journey
            Join<Rating, Journey> ratingJourney = ratingRoot.join(Rating_.JOURNEY);

            // join Journey to Driver
            Join<Journey, Driver> ratingDriver = ratingJourney.join(Journey_.DRIVER);

            // select average rating
            subquery.select(cb.avg(ratingRoot.get(Rating_.VALUE)));

            subquery.where(cb.equal(ratingDriver, root.get(Journey_.DRIVER)));

            // if rating was null use 0.0
            if (showWithoutRating) {
                return cb.or(
                        cb.greaterThanOrEqualTo(cb.coalesce(subquery, 0.0), rating),
                        cb.equal(cb.coalesce(subquery, 0.0), 0.0)
                );
            }
            return cb.greaterThanOrEqualTo(cb.coalesce(subquery, 0.0), rating);
        };
    }
}
