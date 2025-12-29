package hu.ridesharing.repository;

import hu.ridesharing.entity.Journey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JourneyRepository extends JpaRepository<Journey, Long>, JpaSpecificationExecutor<Journey> {
}
