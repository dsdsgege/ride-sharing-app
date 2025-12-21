package hu.ridesharing.repository;

import hu.ridesharing.entity.Route;
import hu.ridesharing.entity.RouteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, RouteId> {

//    @Query(value = "SELECT r FROM Route r WHERE r.longitudeFrom = ?1 AND r.latitudeFrom = ?2 AND " +
//            "r.longitudeTo = ?3 AND r.latitudeTo = ?4")
//    Optional<Route> findByCoordinate(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo);


}
