package hu.ridesharing.repository;

import hu.ridesharing.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, String> {

    @Query("SELECT d FROM driver d WHERE d.username IN :usernames")
    List<Driver> findAllFullNameByUsernames(List<String> usernames);
}
