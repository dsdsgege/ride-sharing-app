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

    @ManyToOne
    @JoinColumn(name = "rated_username")
    private User rated;

    @ManyToOne
    @JoinColumn(name = "rater_username")
    private User rater;

    @Enumerated(EnumType.STRING)
    private RatingType type;

    private double value;
}