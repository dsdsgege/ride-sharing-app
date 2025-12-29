package hu.ridesharing.controller;

import hu.ridesharing.dto.response.incoming.GeocodingReverseResponse;
import hu.ridesharing.service.external.GeocodingApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/geocoding"))
public class GeocodingController {

    private final GeocodingApiService geoCodingApiService;

    @Autowired
    public GeocodingController(GeocodingApiService geoCodingApiService) {
        this.geoCodingApiService = geoCodingApiService;
    }

    @GetMapping("/address")
    public GeocodingReverseResponse[] getAddress(@RequestParam("latitude") double latitude,
                                               @RequestParam("longitude") double longitude) {

        return geoCodingApiService.getReverse(latitude, longitude);
    }
}
