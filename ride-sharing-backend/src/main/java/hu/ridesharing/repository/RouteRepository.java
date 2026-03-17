package hu.ridesharing.repository;

import hu.ridesharing.entity.Route;
import hu.ridesharing.entity.RouteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, RouteId> {
}
