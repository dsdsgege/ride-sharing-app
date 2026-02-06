package hu.ridesharing.dto.request;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class RideFilterRequest {
    private String pickupFrom;
    private String dropOffTo;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private int seats;
    private double maxPrice;
    private double rating;
    private boolean showWithoutRating;
    private int page;
    private int pageSize;
    private String sortBy;
    private String direction;
}
