package hu.ridesharing.controller;

import hu.ridesharing.dto.response.outgoing.ResponseStatus;
import hu.ridesharing.entity.Rating;
import hu.ridesharing.service.JourneyService;
import hu.ridesharing.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    private final JourneyService journeyService;

    @Autowired
    public RatingController(RatingService ratingService, JourneyService journeyService) {
        this.ratingService = ratingService;
        this.journeyService = journeyService;
    }

    @PostMapping("/add/{driveId}")
    public ResponseStatus addRating(@PathVariable Long driveId,
                                    @RequestBody Rating rating,
                                    @AuthenticationPrincipal Jwt jwt) {

        return ratingService.rateMyPassenger(driveId, rating, jwt.getClaimAsString("preferred_username"));
    }
}
