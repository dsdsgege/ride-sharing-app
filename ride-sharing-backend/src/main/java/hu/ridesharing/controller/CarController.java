package hu.ridesharing.controller;

import hu.ridesharing.dto.response.car.CarDTO;
import hu.ridesharing.dto.response.car.CarGenerationDTO;
import hu.ridesharing.dto.response.car.CarModelDTO;
import hu.ridesharing.dto.response.car.CarTrimDTO;
import hu.ridesharing.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/car")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/all")
    public List<CarDTO> findAll() {
        return this.carService.findAll();
    }

    @GetMapping("/make")
    public List<CarDTO> findCarMake(@RequestParam("car_make") String make) {
        return this.carService.findCarMake(make);
    }

    @GetMapping("/model")
    public List<CarModelDTO> findCarModel(@RequestParam("make_id") Long makeId) {
        return this.carService.findCarModelsByMakeId(makeId);
    }

    @GetMapping("/generation")
    public List<CarGenerationDTO> findCarGeneration(@RequestParam("model_id") Long modelId) {
        return this.carService.findCarGenerationsByModelId(modelId);
    }

    @GetMapping("/trim")
    public List<CarTrimDTO> findCarTrim(@RequestParam("generation_id") Long generationId) {
        return this.carService.findCarTrimsByGenerationId(generationId);
    }
}
