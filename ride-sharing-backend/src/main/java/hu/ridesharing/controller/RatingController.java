package hu.ridesharing.controller;

import hu.ridesharing.dto.RatingDTO;
import hu.ridesharing.dto.response.outgoing.ResponseStatus;
import hu.ridesharing.entity.Rating;
import hu.ridesharing.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/add/as-driver/{driveId}")
    public ResponseStatus addRatingAsDriver(@PathVariable Long driveId, @RequestBody Rating rating,
                                    @AuthenticationPrincipal Jwt jwt) {

        return ratingService.rateMyPassenger(driveId, rating, jwt.getClaimAsString("preferred_username"));
    }

    @PostMapping("/add/as-passenger/{rideId}")
    public ResponseStatus addRatingAsPassenger(@PathVariable Long rideId, @RequestBody Rating rating,
                                    @AuthenticationPrincipal Jwt jwt) {

        return ratingService.rateMyDriver(rideId, rating, jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/received/passenger")
    public Page<RatingDTO> getReceivedAsPassenger(@RequestParam int page, @AuthenticationPrincipal Jwt jwt) {
        return ratingService.getReceivedAsPassenger(page, jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/received/driver")
    public Page<RatingDTO> getReceivedAsDriver(@RequestParam int page, @AuthenticationPrincipal Jwt jwt) {
        return ratingService.getReceivedAsDriver(page, jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/given/driver")
    public Page<RatingDTO> getGivenAsDriver(@RequestParam int page, @AuthenticationPrincipal Jwt jwt) {
        return ratingService.getGivenAsDriver(page, jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/given/passenger")
    public Page<RatingDTO> getGivenAsPassenger(@RequestParam int page, @AuthenticationPrincipal Jwt jwt) {
        return ratingService.getGivenAsPassenger(page, jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/my-count")
    public long getMyRatingCount(@AuthenticationPrincipal Jwt jwt) {
        return ratingService.getMyRatingCount(jwt.getClaimAsString("preferred_username"));
    }
}
