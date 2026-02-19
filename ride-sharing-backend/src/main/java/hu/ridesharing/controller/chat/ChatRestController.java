package hu.ridesharing.controller.chat;

import hu.ridesharing.entity.ChatMessage;
import hu.ridesharing.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>This class handles the chats on the database level.</p>
 *
 *  - It stores messages from the client in the database
 *  - It sends older messages to the client from the database
 */
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    @Autowired
    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/partners")
    public Page<String> findChatPartnersByUsername(@RequestParam String username,
                                                   @RequestParam int page) {

        return chatService.findChatPartnersByUsername(username, page);
    }

    @GetMapping("/history")
    public Page<ChatMessage> findMessagesBetweenPartners(@RequestParam(name = "first_partner") String firstPartner,
                                                         @RequestParam(name = "second_partner") String secondPartner,
                                                         @RequestParam int page) {

        return chatService.findMessagesBetweenPartners(firstPartner, secondPartner, page);
    }
}
