package hu.ridesharing.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

/**
 * This class is a composite key for the Car entity.
 */
@Data
@Embeddable
public class CarId implements Serializable {
    private String make;
    private String model;
    private int year;
}
