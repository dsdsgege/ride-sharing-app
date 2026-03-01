package hu.ridesharing.service;

import hu.ridesharing.dto.response.outgoing.JourneyResponseDTO;
import hu.ridesharing.entity.User;
import hu.ridesharing.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    private final JourneyService journeyService;

    @Autowired
    public RatingService(RatingRepository ratingRepository, JourneyService journeyService) {
        this.ratingRepository = ratingRepository;
        this.journeyService = journeyService;
    }

    public Page<JourneyResponseDTO> getEligibleForRating(int page, User user) {
        return journeyService.getEligibleForRating(user, page);
    }
}
