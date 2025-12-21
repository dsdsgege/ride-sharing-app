package hu.ridesharing.controller;

import hu.ridesharing.dto.DriveDTO;
import hu.ridesharing.entity.Drive;
import hu.ridesharing.entity.GeocodingResponse;
import hu.ridesharing.service.DriveService;
import hu.ridesharing.service.PriceCalculatorService;
import hu.ridesharing.service.external.GeocodingApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/drive")

public class DriveController {

    private final PriceCalculatorService priceCalculatorService;
    private final GeocodingApiService geocodingApiService;
    private final DriveService driveService;

    @Autowired
    public DriveController(PriceCalculatorService priceCalculatorService, GeocodingApiService geocodingApiService,
                           DriveService driveService) {
        this.priceCalculatorService = priceCalculatorService;
        this.geocodingApiService = geocodingApiService;
        this.driveService = driveService;
    }

    @GetMapping("/price")
    public Map<String, Integer> getPrice(@RequestParam("pickup_from") String from,
                                         @RequestParam("drop_off_to") String to,
                                         @RequestParam("seats") int seats,
                                         @RequestParam("consumption") int consumption,
                                         @RequestParam("car_price") int price,
                                         @RequestParam("make_year") int makeYear) {

        GeocodingResponse fromResponse = geocodingApiService.getGeocoding(from)[0];
        GeocodingResponse toResponse = geocodingApiService.getGeocoding(to)[0];

        return Map.of("price", priceCalculatorService.getPrice(from, to, fromResponse.getLat(),
                fromResponse.getLon(), toResponse.getLat(), toResponse.getLon(), seats, consumption, price, makeYear));
    }

    @PostMapping("add_drive")
    public Map<String, Boolean> addDrive(@RequestBody Drive drive) {
        return driveService.addDrive(drive) == null ? Map.of("success", false) : Map.of("success", true);
    }
}
