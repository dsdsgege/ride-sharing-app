package hu.ridesharing.controller;

import hu.ridesharing.dto.request.AddDriveRequest;
import hu.ridesharing.entity.Driver;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.GeocodingResponse;
import hu.ridesharing.service.DriverService;
import hu.ridesharing.service.JourneyService;
import hu.ridesharing.service.PriceCalculatorService;
import hu.ridesharing.service.external.GeocodingApiService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/drive")
public class DriveController {

    private final PriceCalculatorService priceCalculatorService;
    private final GeocodingApiService geocodingApiService;
    private final JourneyService journeyService;
    private final DriverService driverService;

    @Autowired
    public DriveController(PriceCalculatorService priceCalculatorService, GeocodingApiService geocodingApiService,
                           JourneyService journeyService, DriverService driverService) {

        this.priceCalculatorService = priceCalculatorService;
        this.geocodingApiService = geocodingApiService;
        this.journeyService = journeyService;
        this.driverService = driverService;
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

    @Transactional(rollbackOn = SQLException.class)
    @PostMapping("add_drive")
    public Map<String, Boolean> addDrive(@RequestBody AddDriveRequest addDriveRequest) throws SQLException {
        Driver driver = addDriveRequest.getDriver();
        Journey drive = addDriveRequest.getDrive();
        drive.setDriver(driver);

        driverService.addDriver(driver);

        var journey = journeyService.addDrive(drive);
        if (journey == null) {
            throw new SQLException("Could not save drive to the database.");
        }
        return Map.of("success", true);
    }

    @GetMapping("/drive-count")
    public int getDriveCountByUsername(@RequestParam String username) {
        return journeyService.getDriveCountByUsername(username);
    }

    @GetMapping("/driver-rating")
    public double getDriverRatingByUsername(@RequestParam String username) {
        return driverService.getDriverRatingByUsername(username);
    }

    @PostMapping("/accept-passenger")
    public AcceptanceStatus acceptPassenger(@RequestBody Token token) {
        return new AcceptanceStatus(driverService.acceptPassenger(token.token()));
    }

    record AcceptanceStatus(boolean success) {
    }

    record Token(String token) {
    }
}
