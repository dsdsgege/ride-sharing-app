package hu.ridesharing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(ChatId.class)
public class Chat {

    @Id
    private String from;

    @Id
    private String to;

    @Id
    private long timestamp;

    private String message;
}
