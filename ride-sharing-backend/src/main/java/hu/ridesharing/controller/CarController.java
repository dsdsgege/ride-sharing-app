package hu.ridesharing.controller;

import hu.ridesharing.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/car")
public class CarController {

    private CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/find/make")
    public List<String> findCarMake(@RequestParam("car_make") String make) {
        return this.carService.findCarMake(make);
    }
}
