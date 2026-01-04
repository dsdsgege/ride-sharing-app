package hu.ridesharing.dto.response.outgoing;

import hu.ridesharing.dto.DriverDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JourneyResponseDTO {
    private String fromCity;
    private String toCity;
    private double price;
    private DriverDTO driver;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String carMake;
    private int seats;
}
