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

    /**
     * The sender's username.
     */
    @Id
    private String sender;

    /**
     * The receiver's username.
     */
    @Id
    private String receiver;

    /**
     * The timestamp of the message. (ONLY ONE TIMEZONE!)
     */
    @Id
    private Long timestamp;

    private String message;
}
