package hu.ridesharing.repository.car;

import hu.ridesharing.entity.car.CarGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarGenerationRepository extends JpaRepository<CarGeneration, Long> {
    boolean existsByModelId(Long modelId);
    List<CarGeneration> findAllByModelId(Long modelId);
}