package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "driver")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @OneToMany(mappedBy = "driver")
    private Set<Rating> ratings;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "driver")
    private Set<Journey> drives;
}
