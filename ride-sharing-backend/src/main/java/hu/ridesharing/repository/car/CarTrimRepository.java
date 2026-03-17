package hu.ridesharing.repository.car;

import hu.ridesharing.entity.car.CarTrim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarTrimRepository extends JpaRepository<CarTrim, Long> {
    boolean existsByGenerationId(Long generationId);
    List<CarTrim> findAllByGenerationId(Long generationId);
}