package hu.ridesharing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "users") // 'user' is used in postgres
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String username;

    private String fullName;

    private String emailAddress;
}
