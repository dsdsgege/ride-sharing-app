package hu.ridesharing.dto;

import hu.ridesharing.entity.RatingType;
import hu.ridesharing.entity.User;
import lombok.Data;

@Data
public class RatingDTO {
    private double value;
    private String comment;
    private User rater;
    private User rated;
    private RatingType type;
}
