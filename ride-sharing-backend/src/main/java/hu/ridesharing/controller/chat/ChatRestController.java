package hu.ridesharing.controller.chat;

import org.springframework.web.bind.annotation.RequestMapping;
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

}
