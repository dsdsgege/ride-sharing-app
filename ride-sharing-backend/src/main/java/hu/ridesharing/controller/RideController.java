package hu.ridesharing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/rides")
@RestController
public class RideController {

    @GetMapping("/price")
    public int getPrice(@RequestParam("pickup_from") String from, @RequestParam("drop_off_to") String to,
                        @RequestParam("seats") int seats,  @RequestParam("consumption") int consumption) {
        return 100;
    }
}
