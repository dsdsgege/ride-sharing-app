package hu.ridesharing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "driver")
public class Driver {

    @Id
    private String name;

    private double rating;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "driver")
    private Set<Drive> drives;
}
