package hu.ridesharing.controller;

import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/{id}")
    public JourneyResponseDTO getRide(@PathVariable Long id) {
        return journeyService.getRide(id);
    }

    @GetMapping("/ride-count")
    public int getRideCountByFullName(@RequestParam(name = "full_name") String fullName) {
        return journeyService.getRideCountByFullName(fullName);
    }
}
