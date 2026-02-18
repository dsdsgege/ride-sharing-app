package hu.ridesharing.repository;

import hu.ridesharing.entity.ChatId;
import hu.ridesharing.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<ChatMessage, ChatId>, JpaSpecificationExecutor<ChatMessage> {

    @Query(value = "SELECT CASE WHEN m.sender = :username THEN m.receiver ELSE m.sender END AS partner " +
            "FROM chat_message m " +
            "WHERE m.sender = :username OR m.receiver = :username " +
            "GROUP BY partner " +
            "ORDER BY MAX(timestamp) DESC",
            countQuery =
                    "SELECT count(DISTINCT CASE WHEN m.sender = :username THEN m.receiver ELSE m.sender END) " +
                    "FROM chat_message m " +
                    "WHERE m.sender = :username OR m.receiver = :username",
            nativeQuery = true)
    Page<String> findChatPartners(@Param("username") String username, Pageable pageable);
}
