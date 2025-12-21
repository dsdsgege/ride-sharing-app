package hu.ridesharing.repository;

import hu.ridesharing.entity.Car;
import hu.ridesharing.entity.CarId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, CarId> {

    @Query("SELECT c.make FROM Car c WHERE c.make LIKE %?1%")
    List<String> findCarMake(String pattern);
}
