package hu.ridesharing.repository.specification;

import hu.ridesharing.entity.ChatMessage;
import org.springframework.data.jpa.domain.Specification;

public final class ChatSpecificationFactory {

    private ChatSpecificationFactory() {
    }

    public static Specification<ChatMessage> findByFromAndTo(String from, String to) {
        return (root, query, cb) -> cb.and(cb.equal(root.get("from"), from),
                cb.equal(root.get("to"), to));
    }
}
