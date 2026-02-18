package hu.ridesharing.service;

import hu.ridesharing.entity.ChatMessage;
import hu.ridesharing.exception.ChatException;
import hu.ridesharing.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void save(ChatMessage chatMessage) {
        try {
            chatRepository.save(chatMessage);
        } catch (Exception e) {
            throw new ChatException("Could not save chat message to the database.");
        }
    }

    public Page<String> findChatPartnersByUsername(String username, int page) {
        return chatRepository.findChatPartners(username, PageRequest.of(0, (page + 1) * 5));
    }
}
