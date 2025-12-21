package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Drive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCity;

    private String toCity;

    @ManyToOne
    @JoinColumn(name = "driver_name")
    private Driver driver;

    private LocalDateTime depart;

    private LocalDateTime arrive;

    private String carMake;

    private String modelYear;

    private int seats;

    private double passengerPrice;
}
