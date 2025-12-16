package hu.ridesharing.repository;

import hu.ridesharing.entity.Car;
import hu.ridesharing.entity.CarId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, CarId> {
}
