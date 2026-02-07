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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return make.equals(((CarId) o).getMake())
                && model.equals(((CarId) o).getModel())
                && year == ((CarId) o).getYear();
    }

    @Override
    public int hashCode() {
        return make.hashCode() + model.hashCode() + year;
    }
}
