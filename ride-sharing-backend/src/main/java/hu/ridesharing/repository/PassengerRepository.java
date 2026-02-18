package hu.ridesharing.repository;

import hu.ridesharing.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, String> {

    @Query("SELECT p FROM Passenger p WHERE p.username IN :usernames")
    List<Passenger> findAllUsernames(List<String> usernames);
}
