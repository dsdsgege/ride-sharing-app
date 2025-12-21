package hu.ridesharing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class GeocodingResponse {

    @Id
    private String city;

    private double lat;

    private double lon;

    private String country;
}
