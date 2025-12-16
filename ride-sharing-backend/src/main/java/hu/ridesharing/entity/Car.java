package hu.ridesharing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
/**
 * This class represents a car entity in the database. It has a composite key of the car make,
 * car model, and the make year.
 */
@Data
@IdClass(CarId.class)
@Entity
public class Car {
    @Id
    private String make;

    @Id
    private String model;

    @Id
    private int year;

    private int price;
}
