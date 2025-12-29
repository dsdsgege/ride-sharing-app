package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @ManyToMany(mappedBy = "passengers")
    private Set<Journey> rides;

    @OneToMany(mappedBy = "passenger")
    private Set<Rating> ratings;
}
