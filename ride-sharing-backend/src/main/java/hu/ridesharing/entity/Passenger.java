package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Passenger {

    @Id
    @EqualsAndHashCode.Include
    private String username;

    private String fullName;

    @ToString.Exclude
    @ManyToMany(mappedBy = "passengers")
    private Set<Journey> rides;
}
