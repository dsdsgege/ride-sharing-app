package hu.ridesharing.repository;

import hu.ridesharing.entity.GeocodingResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeocodingResponseRepository extends JpaRepository<GeocodingResponse, String> {
}
