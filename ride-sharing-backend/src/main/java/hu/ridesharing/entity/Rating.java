package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "journey_id")
    private Journey journey;

    private double value;

    private RatingType type;

    private String comment;
}

enum RatingType {
    PASSENGER_TO_DRIVER,
    DRIVER_TO_PASSENGER
}
