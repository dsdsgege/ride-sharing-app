package hu.ridesharing.dto.response.outgoing;

import hu.ridesharing.dto.DriverDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JourneyResponseDTO {
    private Long id;
    private String fromCity;
    private String toCity;
    private double price;
    private DriverDTO driver;
    private LocalDateTime depart;
    private LocalDateTime arrive;
    private String carMake;
    private int seats;
}
