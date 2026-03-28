package hu.ridesharing.controller;

import hu.ridesharing.dto.request.AddDriveRequest;
import hu.ridesharing.dto.response.outgoing.JourneyResponseWithPassengersDTO;
import hu.ridesharing.dto.response.outgoing.ResponseStatus;
import hu.ridesharing.entity.User;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.GeocodingResponse;
import hu.ridesharing.exception.BadRequestException;
import hu.ridesharing.service.JourneyService;
import hu.ridesharing.service.PriceCalculatorService;
import hu.ridesharing.service.UserService;
import hu.ridesharing.service.external.GeocodingApiService;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/drive")
public class DriveController {

    private final PriceCalculatorService priceCalculatorService;
    private final GeocodingApiService geocodingApiService;
    private final JourneyService journeyService;
    private final UserService userService;

    @Autowired
    public DriveController(PriceCalculatorService priceCalculatorService, GeocodingApiService geocodingApiService,
                           JourneyService journeyService, UserService userService) {

        this.priceCalculatorService = priceCalculatorService;
        this.geocodingApiService = geocodingApiService;
        this.journeyService = journeyService;
        this.userService = userService;
    }

    @GetMapping("/price")
    public Map<String, Integer> getPrice(@RequestParam("pickup_from") String from,
                                         @RequestParam("drop_off_to") String to,
                                         @RequestParam("trim_id") Long trimId,
                                         @RequestParam("given_seats") int givenSeats,
                                         @RequestParam("car_price") int price) {

        GeocodingResponse fromResponse = geocodingApiService.getGeocoding(from)[0];
        GeocodingResponse toResponse = geocodingApiService.getGeocoding(to)[0];

        return Map.of("price", priceCalculatorService.getPrice(from, to, fromResponse.getLat(),
                fromResponse.getLon(), toResponse.getLat(), toResponse.getLon(), trimId, price, givenSeats));
    }

    @Transactional(rollbackOn = SQLException.class)
    @PostMapping("add_drive")
    public Map<String, Boolean> addDrive(@RequestBody AddDriveRequest addDriveRequest,
                                         @AuthenticationPrincipal Jwt jwt) throws SQLException {
                                            
        User driver = addDriveRequest.driver();
        userService.saveUser(driver);

        Journey drive = addDriveRequest.drive();
        drive.setDriver(driver);

        if (drive.getArrive() == null || drive.getDepart() == null || StringUtils.isBlank(drive.getCarMake())
                || drive.getSeats() == 0 || StringUtils.isBlank(drive.getFromCity())
                || StringUtils.isBlank(drive.getToCity())) {
            throw new BadRequestException("Some of the fields are empty.");
        }

        var journey = journeyService.addDrive(drive, jwt.getClaimAsString("preferred_username"));
        if (journey == null) {
            throw new SQLException("Could not save drive to the database.");
        }
        return Map.of("success", true);
    }

    @GetMapping("/drive-count")
    public long getDriveCountByUsername(@RequestParam String username) {
        return journeyService.getDriveCountByUsername(username);
    }

    @GetMapping("/driver-rating")
    public double getDriverRatingByUsername(@RequestParam String username) {
        return userService.getDriverRatingByUsername(username);
    }

    @PostMapping("/accept-passenger")
    public ResponseStatus acceptPassenger(@RequestBody Token token) {
        return new ResponseStatus(userService.acceptPassenger(token.token()));
    }

    @GetMapping ("/my-drives")
    public Page<JourneyResponseWithPassengersDTO> getMyRides(@RequestParam int page, @AuthenticationPrincipal Jwt jwt) {
        return journeyService.getMyDrives(jwt.getClaimAsString("preferred_username"), page);
    }

    @DeleteMapping("/my-drive/{driveId}")
    public ResponseStatus deleteDrive(@PathVariable Long driveId, @AuthenticationPrincipal Jwt jwt) {
        return new ResponseStatus(journeyService.deleteDrive(driveId, jwt.getClaimAsString("preferred_username")));
    }

    @PutMapping("/my-drive")
    public ResponseStatus updateDrive(@RequestBody Journey drive, @AuthenticationPrincipal Jwt jwt) {
        return new ResponseStatus(journeyService.updateDrive(drive, jwt.getClaimAsString("preferred_username")));
    }

    public record Token(String token) {
    }
}
