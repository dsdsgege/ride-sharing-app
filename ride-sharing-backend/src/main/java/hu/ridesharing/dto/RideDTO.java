package hu.ridesharing.dto;

import hu.ridesharing.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RideDTO {
    private Driver driver;
    private String from;
    private String to;
    private LocalDateTime depart;
    private LocalDateTime arrive;
    private double passengerPrice;
    private int seats;
}
