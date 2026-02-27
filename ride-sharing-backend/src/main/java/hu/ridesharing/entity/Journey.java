package hu.ridesharing.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL)
    private Set<JourneyPassenger> passengers;

    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL)
    private Set<Rating> ratings;

    @JsonProperty("departureTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime depart;

    @JsonProperty("arrivalTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrive;

    private String carMake;

    private String modelYear;

    private int seats;

    private double passengerPrice;
}
