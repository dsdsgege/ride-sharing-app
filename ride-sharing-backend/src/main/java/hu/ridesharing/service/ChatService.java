package hu.ridesharing.service;

import hu.ridesharing.entity.ChatMessage;
import hu.ridesharing.exception.ChatException;
import hu.ridesharing.repository.ChatRepository;
import hu.ridesharing.repository.specification.ChatSpecificationFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public Page<ChatMessage> findMessagesBetweenPartners(String firstPartner, String secondPartner, int page) {
        Specification<ChatMessage> spec = ChatSpecificationFactory.messagesBetweenPartners(firstPartner, secondPartner);

        //TODO: INCREMENTAL FETCH INSTEAD OF FETCHING ALWAY ALL OF (page + 1) * 10
        var messagesPage = chatRepository.findAll(
                spec, PageRequest.of(0, (page + 1) * 10, Sort.by("timestamp").descending())
        );

        // we change the direction of the messages, so the frontend prints it ascending order
        List<ChatMessage> reversedContent = new ArrayList<>(messagesPage.getContent());
        Collections.reverse(reversedContent);

        // reconstruct the Page object
        return new PageImpl<>(reversedContent, messagesPage.getPageable(), messagesPage.getTotalElements());
    }
}
