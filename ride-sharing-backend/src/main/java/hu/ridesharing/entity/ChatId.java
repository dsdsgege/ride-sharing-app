package hu.ridesharing.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ChatId implements Serializable {
    private String from;
    private String to;
    private long timestamp;
}
