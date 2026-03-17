package hu.ridesharing.service;

import hu.ridesharing.dto.response.car.CarDTO;
import hu.ridesharing.dto.response.car.CarGenerationDTO;
import hu.ridesharing.dto.response.car.CarModelDTO;
import hu.ridesharing.dto.response.car.CarTrimDTO;
import hu.ridesharing.entity.car.Car;
import hu.ridesharing.entity.car.CarGeneration;
import hu.ridesharing.entity.car.CarModel;
import hu.ridesharing.entity.car.CarTrim;
import hu.ridesharing.repository.car.CarGenerationRepository;
import hu.ridesharing.repository.car.CarModelRepository;
import hu.ridesharing.repository.car.CarRepository;
import hu.ridesharing.repository.car.CarTrimRepository;
import hu.ridesharing.service.external.RapidApiCarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CarService {

    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;
    private final CarGenerationRepository carGenerationRepository;
    private final CarTrimRepository carTrimRepository;
    private final RapidApiCarService rapidApiCarService;

    @Autowired
    public CarService(CarRepository carRepository,
                      CarModelRepository carModelRepository,
                      CarGenerationRepository carGenerationRepository,
                      CarTrimRepository carTrimRepository,
                      RapidApiCarService rapidApiCarService) {

        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.carGenerationRepository = carGenerationRepository;
        this.carTrimRepository = carTrimRepository;
        this.rapidApiCarService = rapidApiCarService;
    }
    public List<CarDTO> findAll() {
        return carRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<CarDTO> findCarMake(String pattern) {
        log.debug("Searching for car make containing: {}", pattern);
        return carRepository.findByNameContainingIgnoreCase(pattern)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<CarModelDTO> findCarModelsByMakeId(Long makeId) {
        log.debug("Searching for car models by make ID: {}", makeId);
        if (!carModelRepository.existsByCarId(makeId)) {
            Car parentCar = carRepository.findById(makeId)
                    .orElseThrow(() -> new IllegalArgumentException("Make not found"));
            rapidApiCarService.fetchAndSaveModels(parentCar);
        }

        return carModelRepository.findAllByCarId(makeId)
                .stream().map(this::mapToDTO).toList();
    }

    public List<CarGenerationDTO> findCarGenerationsByModelId(Long modelId) {
        if (!carGenerationRepository.existsByModelId(modelId)) {
            CarModel parentModel = carModelRepository.findById(modelId)
                    .orElseThrow(() -> new IllegalArgumentException("Model not found"));
            rapidApiCarService.fetchAndSaveGenerations(parentModel);
        }

        return carGenerationRepository.findAllByModelId(modelId)
                .stream().map(this::mapToDTO).toList();
    }

    public List<CarTrimDTO> findCarTrimsByGenerationId(Long generationId) {
        if (!carTrimRepository.existsByGenerationId(generationId)) {
            CarGeneration parentGeneration = carGenerationRepository.findById(generationId)
                    .orElseThrow(() -> new IllegalArgumentException("Generation not found"));
            rapidApiCarService.fetchAndSaveTrims(parentGeneration);
        }

        return carTrimRepository.findAllByGenerationId(generationId)
                .stream().map(this::mapToDTO).toList();
    }

    private CarDTO mapToDTO(Car car) {
        return new CarDTO(car.getId(), car.getName());
    }

    private CarModelDTO mapToDTO(CarModel carModel) {
        return new CarModelDTO(carModel.getId(), carModel.getName());
    }

    private CarGenerationDTO mapToDTO(CarGeneration gen) {
        return new CarGenerationDTO(gen.getId(), gen.getName(), gen.getYearFrom(), gen.getYearTo());
    }

    private CarTrimDTO mapToDTO(CarTrim trim) {
        return new CarTrimDTO(trim.getId(), trim.getTrim());
    }
}
