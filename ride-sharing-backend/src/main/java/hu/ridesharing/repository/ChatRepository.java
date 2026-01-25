package hu.ridesharing.repository;

import hu.ridesharing.entity.Chat;
import hu.ridesharing.entity.ChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChatRepository extends JpaRepository<Chat, ChatId>, JpaSpecificationExecutor<Chat> {
}
