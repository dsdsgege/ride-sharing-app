package hu.ridesharing.service;

import hu.ridesharing.dto.response.outgoing.ResponseStatus;
import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.Rating;
import hu.ridesharing.entity.RatingType;
import hu.ridesharing.entity.User;
import hu.ridesharing.exception.RatingException;
import hu.ridesharing.repository.RatingRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseStatus rateMyPassenger(Long driveId, Rating rating, String username) {
        if (StringUtils.isBlank(rating.getComment())) {
            throw new RatingException("Comment must not be empty");
        }

        Journey journey = journeyService.checkMyJourney(username, driveId);

        User driver = journey.getDriver();

        rating.setJourney(journey);
        rating.setRater(driver);
        rating.setType(RatingType.DRIVER_TO_PASSENGER);
        ratingRepository.save(rating);
        return new ResponseStatus(true);
    }
}
