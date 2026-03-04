package hu.ridesharing.controller;

import hu.ridesharing.dto.request.JoinRideRequest;
import hu.ridesharing.dto.request.RideFilterRequest;
import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.dto.response.outgoing.ResponseStatus;
import hu.ridesharing.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/ride")
public class RideController {

    private final JourneyService journeyService;

    @Autowired
    public RideController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping("/rides")
    public Page<JourneyResponseDTO> getRides(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
                                             @RequestParam(name = "sort_by", defaultValue = "depart") String sortBy,
                                             @RequestParam(defaultValue = "asc") String direction,
                                             @RequestParam(name = "from") String fromCity,
                                             @RequestParam(name = "to") String toCity,
                                             @RequestParam(name = "date_from") OffsetDateTime dateFrom,
                                             @RequestParam(name = "date_to") OffsetDateTime dateTo) {

        var sort = Sort.by(sortBy);
        return journeyService.getRides(fromCity, toCity, dateFrom.toLocalDateTime(), dateTo.toLocalDateTime(),
                PageRequest.of(page, pageSize, "desc".equalsIgnoreCase(direction) ? sort.descending() : sort));
    }

    @PostMapping("/rides/filter")
    public Page<JourneyResponseDTO> filterRides(@RequestBody RideFilterRequest filterRequest) {
        return journeyService.getRides(filterRequest);
    }

    @GetMapping("/{id}")
    public JourneyResponseDTO getRide(@PathVariable Long id) {
        return journeyService.getRide(id);
    }

    @GetMapping("/my-rides")
    public Page<JourneyResponseDTO> getMyRides(@RequestParam int page, @AuthenticationPrincipal Jwt jwt) {
        return journeyService.getMyRides(page, jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/ride-count")
    public long getRideCountByUsername(@RequestParam String username) {
        return journeyService.getRideCountByUsername(username);
    }

    @PostMapping("/join/{id}")
    public ResponseStatus joinRide(@PathVariable Long id, @RequestBody JoinRideRequest request) {
        return this.journeyService.joinRide(id, request.username(), request.email(), request.fullName());
    }
}
