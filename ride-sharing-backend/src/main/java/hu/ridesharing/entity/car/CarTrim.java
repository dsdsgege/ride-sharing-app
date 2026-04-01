package hu.ridesharing.entity.car;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class CarTrim {

    @Id
    private Long id;

    private String trim;

    private Integer numberOfSeats;

    private Double mixedFuelConsumptionPer100KmL;

    private String engineType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id")
    @JsonIgnore // there is generation in the response too with string value...
    private CarGeneration generation;
}