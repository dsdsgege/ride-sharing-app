package hu.ridesharing.service;

import hu.ridesharing.dto.RatingDTO;
import hu.ridesharing.dto.response.outgoing.ResponseStatus;
import hu.ridesharing.entity.*;
import hu.ridesharing.exception.RatingException;
import hu.ridesharing.repository.RatingRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public long getMyRatingCount(String username) {
        User user = new User();
        user.setUsername(username);
        return this.ratingRepository.countByRated(user) + this.ratingRepository.countByRater(user);
    }

    public Page<RatingDTO> getReceivedAsPassenger(int page, String username) {
        User passenger = new User();
        passenger.setUsername(username);
        return ratingRepository.findByPassengerRated(passenger, PageRequest.of(0, (page + 1) * 5))
                .map(this::mapToDTO);
    }

    public Page<RatingDTO> getReceivedAsDriver(int page, String username) {
        User driver = new User();
        driver.setUsername(username);
        return ratingRepository.findByDriverRated(driver, PageRequest.of(0, (page + 1) * 5))
                .map(this::mapToDTO);
    }

    public Page<RatingDTO> getGivenAsDriver(int page, String username) {
        User driver = new User();
        driver.setUsername(username);
        return ratingRepository.findByDriverRater(driver, PageRequest.of(0, (page + 1) * 5))
                .map(this::mapToDTO);
    }

    public Page<RatingDTO> getGivenAsPassenger(int page, String username) {
        User passenger = new User();
        passenger.setUsername(username);
        return ratingRepository.findByPassengerRater(passenger, PageRequest.of(0, (page + 1) * 5))
                .map(this::mapToDTO);
    }

    public ResponseStatus rateMyPassenger(Long driveId, Rating rating, String username) {
        if (StringUtils.isBlank(rating.getComment())) {
            throw new RatingException("Comment must not be empty");
        }

        Journey journey = journeyService.checkMyJourney(username, driveId);

        User driver = journey.getDriver();

        RatingId id = new RatingId();
        id.setRated(rating.getRated().getUsername());
        id.setRater(driver.getUsername());
        id.setJourney(journey.getId());
        id.setType(RatingType.DRIVER_TO_PASSENGER);

        if (ratingRepository.existsById(id)) {
            throw new RatingException("You have already rated this passenger.");
        }

        rating.setJourney(journey);
        rating.setRater(driver);
        rating.setType(RatingType.DRIVER_TO_PASSENGER);
        ratingRepository.save(rating);
        return new ResponseStatus(true);
    }

    public ResponseStatus rateMyDriver(Long rideId, Rating rating, String username) {
        if (StringUtils.isBlank(rating.getComment())) {
            throw new RatingException("Comment must not be empty");
        }

        Journey journey = journeyService.checkMyJourney(username, rideId);

        User passenger = new User();
        passenger.setUsername(username);

        RatingId ratingId = new RatingId();
        ratingId.setRated(journey.getDriver().getUsername());
        ratingId.setRater(passenger.getUsername());
        ratingId.setJourney(journey.getId());
        ratingId.setType(RatingType.PASSENGER_TO_DRIVER);

        if (ratingRepository.existsById(ratingId)) {
            throw new RatingException("You have already rated this driver.");
        }

        rating.setJourney(journey);
        rating.setRater(passenger);
        rating.setType(RatingType.PASSENGER_TO_DRIVER);
        ratingRepository.save(rating);
        return new ResponseStatus(true);
    }

    private RatingDTO mapToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setValue(rating.getValue());
        dto.setComment(rating.getComment());
        dto.setRated(rating.getRated());
        dto.setRater(rating.getRater());
        dto.setType(rating.getType());
        return dto;
    }
}
