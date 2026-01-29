package hu.ridesharing.controller;

import hu.ridesharing.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/private-message")
    public ChatMessage sendMessage(@Payload ChatMessage chat) {
        chat.setTimestamp(System.currentTimeMillis());

        // this way we do not need to make complex url's on the frontend
        messagingTemplate.convertAndSend("/topic/private-messages/" + chat.getReceiver(), chat);

        // sending it to the sender as well, so they both see the sent message
        messagingTemplate.convertAndSend("/topic/private-messages/" + chat.getSender(), chat);

        return chat;
    }

    @MessageMapping("/add-user")
    public void addUser(@Payload ChatMessage chat, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("fullName", chat.getSender());
    }
}
