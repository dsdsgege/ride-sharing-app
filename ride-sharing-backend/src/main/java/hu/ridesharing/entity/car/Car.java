package hu.ridesharing.entity.car;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a car entity in the database. Its id is coming from external RAPID API.
 * <p>
 * All the related classes mirror the external API architecture, so we can cache data from our database
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Car {

    // not generated, comes from external API
    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarModel> models = new ArrayList<>();
}
