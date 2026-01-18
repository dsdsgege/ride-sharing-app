package hu.ridesharing.repository;

import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JourneyRepository extends JpaRepository<Journey, Long>, JpaSpecificationExecutor<Journey> {
    List<Journey> findByPassengers(Passenger passenger);
    List<Journey> findByDriver(Driver driver);
}
