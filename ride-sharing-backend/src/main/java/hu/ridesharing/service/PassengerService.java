package hu.ridesharing.service;

import hu.ridesharing.entity.Passenger;
import hu.ridesharing.repository.PassengerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public Map<String, String> findFullNameMapByUsernames(List<String> usernames) {
        return this.passengerRepository.findAllUsernames(usernames)
                .stream()
                .collect(
                        Collectors.toMap(Passenger::getUsername, Passenger::getFullName)
                );
    }
}
