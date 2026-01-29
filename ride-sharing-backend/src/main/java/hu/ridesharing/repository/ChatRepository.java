package hu.ridesharing.repository;

import hu.ridesharing.entity.ChatId;
import hu.ridesharing.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatRepository extends JpaRepository<ChatMessage, ChatId>, JpaSpecificationExecutor<ChatMessage> {
}
