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

        // sending it to the common topic
        messagingTemplate.convertAndSend("/topic/private-messages/" +
                getRoomName(chat.getReceiver(), chat.getSender()), chat);

        chatService.save(chat);
    }

    /**
     * This method creates a determinstic room name for the two users.
     *
     * @param user1
     * @param user2
     * @return
     */
    private String getRoomName(String user1, String user2) {
        if (user1.compareTo(user2) < 0) {
            return user1 + "-" + user2;
        } else {
            return user2 + "-" + user1;
        }
    }
}
