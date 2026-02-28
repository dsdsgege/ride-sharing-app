package hu.ridesharing.service;

import hu.ridesharing.entity.Journey;
import hu.ridesharing.entity.JourneyPassenger;
import hu.ridesharing.entity.Rating;
import hu.ridesharing.entity.User;
import hu.ridesharing.exception.DriverNotFoundException;
import hu.ridesharing.repository.JourneyPassengerRepository;
import hu.ridesharing.repository.RatingRepository;
import hu.ridesharing.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * This class handles everything related to the users.
 *
 * <p>Keep in mind a user can be a driver and passenger at the same time.</p>
 */
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final RatingRepository ratingRepository;

    private final JourneyPassengerRepository journeyPassengerRepository;

    private static final String ERROR_MESSAGE = "Driver with username %s not found!";

    @Autowired
    public UserService(UserRepository userRepository, RatingRepository ratingRepository,
                       JourneyPassengerRepository journeyPassengerRepository) {

        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.journeyPassengerRepository = journeyPassengerRepository;
    }

    public void saveUser(User user) {
        if (userRepository.findById(user.getUsername()).isEmpty()) {
            userRepository.save(user);
        }
    }

    public double getDriverRatingByUsername(String username) {
        return ratingRepository.findByDriver(
                        userRepository.findById(username).orElseThrow(
                                () -> new DriverNotFoundException(ERROR_MESSAGE.formatted(username))
                        )).stream()
                .mapToDouble(Rating::getValue)
                .average()
                .orElse(0);
    }

    @Transactional
    public boolean acceptPassenger(String token) {
        Optional<JourneyPassenger> optionalJp = journeyPassengerRepository.findById(token);
        if (optionalJp.isEmpty()) {
            return false;
        }
        JourneyPassenger jp = optionalJp.get();
        jp.setAccepted(true);

        Journey journey = jp.getJourney();
        journey.setSeats(journey.getSeats() - 1);

        return true;
    }

    /**
     * This method finds the full name of the users by their usernames.
     *
     * <p>Note: As the incoming list is ordered by timestamp, from the ChatMessage model, we have to keep that order
     * in the response</p>
     *
     * @param usernames
     * @return
     */
    public Map<String, String> findAllFullNameByUsernames(List<String> usernames) {
        return userRepository.findAllFullNameByUsernames(usernames).stream()
                .collect(
                        Collectors.toMap(User::getUsername, User::getFullName)
                );
    }
}
