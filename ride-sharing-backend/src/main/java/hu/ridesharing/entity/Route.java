package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"latitudeFrom", "longitudeFrom", "latitudeTo", "longitudeTo"})
})
@IdClass(RouteId.class)
public class Route {

    @Id
    private String cityA;

    @Id
    private String cityB;

    private double latitudeFrom;

    private double longitudeFrom;

    private double latitudeTo;

    private double longitudeTo;

    private double distance;

    private double duration;
}
