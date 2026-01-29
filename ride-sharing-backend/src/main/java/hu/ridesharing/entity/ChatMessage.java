package hu.ridesharing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(ChatId.class)
@Getter
@Setter
public class ChatMessage {

    @Id
    private String sender;

    @Id
    private String receiver;

    @Id
    private Long timestamp;

    private String message;
}
