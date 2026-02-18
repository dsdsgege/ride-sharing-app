package hu.ridesharing.controller.chat;

import hu.ridesharing.entity.ChatMessage;
import hu.ridesharing.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


/**
 * This class handles the websocket communication.
 */
@Controller
@MessageMapping("/chat")
public class ChatSimpController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    @Autowired
    public ChatSimpController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/private-messages")
    public void sendMessage(@Payload ChatMessage chat) {
        if (chat.getTimestamp() == null || chat.getTimestamp() == 0) {
            chat.setTimestamp(System.currentTimeMillis());
        }

        // this way we do not need to make complex url's on the frontend
        messagingTemplate.convertAndSend("/topic/private-messages/" + chat.getReceiver(), chat);

        // sending it to the sender as well, so they both see the sent message
        messagingTemplate.convertAndSend("/topic/private-messages/" + chat.getSender(), chat);

        chatService.save(chat);
    }
}
