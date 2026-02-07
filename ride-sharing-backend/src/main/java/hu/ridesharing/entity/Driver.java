package hu.ridesharing.entity;

import jakarta.persistence.*;
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
    private String username;

    private String fullName;

    private String emailAddress;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "driver")
    private Set<Journey> drives;
}
