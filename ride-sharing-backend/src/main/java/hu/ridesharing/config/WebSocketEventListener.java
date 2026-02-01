package hu.ridesharing.config;

import hu.ridesharing.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void webSocketDisconnectHandler(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getSessionAttributes().get("username").toString();
        if (username != null) {
            log.info("Disconnected: {}", username);
            ChatMessage chat= new ChatMessage();
            chat.setSender(username);
            chat.setMessage(username + " left the chat.");

            messagingTemplate.convertAndSend("/topic/private-messages/" + chat.getSender(), chat);
            messagingTemplate.convertAndSend("/topic/private-messages/" + chat.getReceiver(), chat);
        }
        log.info("Disconnected: {}", event);
    }
}
