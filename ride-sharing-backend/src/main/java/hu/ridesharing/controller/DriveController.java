package hu.ridesharing.controller;

import hu.ridesharing.dto.DriveDTO;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/drive")
@RestController
public class DriveController {


    @GetMapping("/price")
    public Map<String, Integer> getPrice(@RequestParam("pickup_from") String from, @RequestParam("drop_off_to") String to,
                        @RequestParam("seats") int seats, @RequestParam("consumption") int consumption) {
        return Map.of("price", 100);
    }

    @PostMapping("add_drive")
    public Map<String, Boolean> addDrive(@RequestBody DriveDTO ride) {
        return Map.of("success", true);
    }
}
