package hu.ridesharing.repository.car;

import hu.ridesharing.entity.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByNameContainingIgnoreCase(String pattern);
}
