package hu.ridesharing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles everything related to the users.
 *
 * <p>Keep in mind a user can be a driver and passenger at the same time.</p>
 */
@Service
@Slf4j
public class UserService {

    private final DriverService driverService;

    private final PassengerService passengerService;

    @Autowired
    public UserService(DriverService driverService, PassengerService passengerService) {
        this.driverService = driverService;
        this.passengerService = passengerService;
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
        Map<String, String> passengers = passengerService.findFullNameMapByUsernames(usernames);

        List<String> notFetched = usernames.stream()
                .filter(username -> passengers.values().stream().noneMatch(id -> id.equals(username)))
                .toList();

        Map<String, String> users = new HashMap<>(passengers);
        if (!notFetched.isEmpty()) {
            users.putAll(driverService.findAllFullNameByUsernames(notFetched));
        }

        return users;
    }
}
