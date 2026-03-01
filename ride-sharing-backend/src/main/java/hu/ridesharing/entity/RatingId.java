package hu.ridesharing.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RatingId implements Serializable {
    private Long journey; // type of journey id
    private String rated;  // type of User id
    private String rater;  // type of User id
    private RatingType type;
}
