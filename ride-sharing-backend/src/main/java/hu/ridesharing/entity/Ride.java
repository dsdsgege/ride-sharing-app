package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class Ride {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver")
    private Driver driver;

    private String departFrom;

    private String arriveTo;

    private LocalDateTime depart;

    private LocalDateTime arrive;

    private double passengerPrice;
}
