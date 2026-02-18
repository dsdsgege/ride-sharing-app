package hu.ridesharing.repository.specification;

import hu.ridesharing.entity.ChatMessage;
import org.springframework.data.jpa.domain.Specification;

public final class ChatSpecificationFactory {

    private ChatSpecificationFactory() {
    }

    public static Specification<ChatMessage> findByUsername(String username) {
        return (root, query, cb) ->
                cb.or(cb.equal(root.get("sender"), username), cb.equal(root.get("receiver"), username));
    }
}
