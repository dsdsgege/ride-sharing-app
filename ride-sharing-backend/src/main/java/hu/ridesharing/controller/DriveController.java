package hu.ridesharing.controller;

import hu.ridesharing.dto.RideDTO;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/rides")
@RestController
public class DriveController {

    @GetMapping("/price")
    public Map<String, Integer> getPrice(@RequestParam("pickup_from") String from, @RequestParam("drop_off_to") String to,
                        @RequestParam("seats") int seats, @RequestParam("consumption") int consumption) {
        return Map.of("price", 100);
    }

    @PostMapping("add_ride")
    public Map<String, Boolean> addRide(@RequestBody RideDTO ride) {
        return Map.of("success", true);
    }
}
