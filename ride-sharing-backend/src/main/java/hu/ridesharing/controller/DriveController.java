package hu.ridesharing.controller;

import hu.ridesharing.dto.DriveDTO;
import hu.ridesharing.service.CarService;
import hu.ridesharing.service.PriceCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/drive")
@RestController
public class DriveController {
    private final PriceCalculatorService priceCalculatorService;

    @Autowired
    public DriveController(PriceCalculatorService priceCalculatorService) {
        this.priceCalculatorService = priceCalculatorService;
    }

    @GetMapping("/price")
    public Map<String, Integer> getPrice(@RequestParam("pickup_from") String from, @RequestParam("drop_off_to") String to,
                        @RequestParam("seats") int seats, @RequestParam("consumption") int consumption) {

        //TODO: coordinates
        return Map.of("price", priceCalculatorService.getPrice(from, to, ));
    }

    @PostMapping("add_drive")
    public Map<String, Boolean> addDrive(@RequestBody DriveDTO ride) {
        return Map.of("success", true);
    }
}
