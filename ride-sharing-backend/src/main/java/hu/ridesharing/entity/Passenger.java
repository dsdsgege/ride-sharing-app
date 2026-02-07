package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "rides")
public class Passenger {

    @Id
    @EqualsAndHashCode.Include
    private String username;

    private String fullName;

    private String emailAddress;

    @OneToMany(mappedBy = "passenger")
    private Set<JourneyPassenger> rides;
}
