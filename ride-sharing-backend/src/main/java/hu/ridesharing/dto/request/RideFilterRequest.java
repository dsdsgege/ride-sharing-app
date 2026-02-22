package hu.ridesharing.dto.request;


import java.time.LocalDateTime;

public record RideFilterRequest (String pickupFrom,
                                String dropOffTo,
                                LocalDateTime dateFrom,
                                LocalDateTime dateTo,
                                int seats,
                                double maxPrice,
                                double rating,
                                boolean showWithoutRating,
                                int page,
                                int pageSize,
                                String sortBy,
                                String direction) {
}
