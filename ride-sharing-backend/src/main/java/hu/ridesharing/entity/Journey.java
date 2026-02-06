package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@ToString(exclude = {"driver", "passengers", "ratings"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String fromCity;

    private String toCity;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToMany
    @JoinTable(
            name = "passenger_journey",
            joinColumns = @JoinColumn(name = "journey_id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_username")
    )
    private Set<Passenger> passengers;

    @OneToMany(mappedBy = "journey")
    private Set<Rating> ratings;

    private LocalDateTime depart;

    private LocalDateTime arrive;

    private String carMake;

    private String modelYear;

    private int seats;

    private double passengerPrice;
}
