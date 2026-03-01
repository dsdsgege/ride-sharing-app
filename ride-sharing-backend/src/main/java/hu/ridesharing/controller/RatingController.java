package hu.ridesharing.controller;

import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.entity.User;
import hu.ridesharing.service.JourneyService;
import hu.ridesharing.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/eligible")
    Page<JourneyResponseDTO> findEligibleForRating(@RequestParam int page,
                                                   @AuthenticationPrincipal Jwt jwt) {

        User user = new User();
        user.setUsername(jwt.getClaimAsString("preferred_username"));
        return ratingService.getEligibleForRating(page, user);
    }
}
