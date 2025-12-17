package hu.ridesharing.service;

import hu.ridesharing.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<String> findCarMake(String pattern) {
        return carRepository.findCarMake(pattern);
    }
}
