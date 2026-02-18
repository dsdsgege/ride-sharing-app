package hu.ridesharing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(ChatId.class)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatMessage {

    /**
     * The sender's username.
     */
    @Id
    @EqualsAndHashCode.Include
    private String sender;

    /**
     * The receiver's username.
     */
    @Id
    @EqualsAndHashCode.Include
    private String receiver;

    /**
     * The timestamp of the message. (ONLY ONE TIMEZONE!)
     */
    @Id
    @EqualsAndHashCode.Include
    private Long timestamp;

    private String message;
}
