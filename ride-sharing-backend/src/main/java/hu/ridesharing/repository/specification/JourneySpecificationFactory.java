package hu.ridesharing.repository.specification;

import hu.ridesharing.entity.Journey;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class JourneySpecificationFactory {

    public static Specification<Journey> findByFromCity(String fromCity) {
        return (root, query, cb) -> cb.equal(root.get("fromCity"), fromCity);
    }

    public static Specification<Journey> findByToCity(String toCity) {
        return (root, query, cb) -> cb.equal(root.get("toCity"), toCity);
    }

    public static Specification<Journey> findByDate(LocalDateTime dateFrom, LocalDateTime dateTo) {
        if (dateFrom == null && dateTo == null) {
            return null;
        }

        if (dateFrom == null) {
            return (root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("depart"), dateTo);
        }

        if (dateTo == null) {
            return (root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("depart"), dateFrom);
        }

        return (root, query, cb) -> cb.between(root.get("depart"), dateFrom, dateTo);
    }
}
