package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"latitudeFrom", "longitudeFrom", "latitudeTo", "longitudeTo"})
})
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double latitudeFrom;

    private double longitudeFrom;

    private double latitudeTo;

    private double longitudeTo;

    private double distance;

    private double duration;
}
