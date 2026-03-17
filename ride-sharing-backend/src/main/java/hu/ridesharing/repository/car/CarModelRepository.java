package hu.ridesharing.repository.car;

import hu.ridesharing.entity.car.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    boolean existsByCarId(Long carId);
    List<CarModel> findAllByCarId(Long carId);
}
