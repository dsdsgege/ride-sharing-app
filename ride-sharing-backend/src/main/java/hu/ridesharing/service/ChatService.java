package hu.ridesharing.service;

import hu.ridesharing.entity.ChatMessage;
import hu.ridesharing.repository.ChatRepository;
import hu.ridesharing.repository.specification.ChatSpecificationFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Page<ChatMessage> getChats(String from, String to, int page, int size) {
        Specification<ChatMessage> spec = ChatSpecificationFactory.findByFromAndTo(from, to);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return chatRepository.findAll(spec, pageRequest);
    }
}
