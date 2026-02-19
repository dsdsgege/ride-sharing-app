package hu.ridesharing.repository.specification;

import hu.ridesharing.entity.ChatMessage;
import hu.ridesharing.entity.ChatMessage_;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public final class ChatSpecificationFactory {

    private ChatSpecificationFactory() {
    }

    public static Specification<ChatMessage> messagesBetweenPartners(String firstPartner, String secondPartner) {
        if (StringUtils.isBlank(firstPartner) || StringUtils.isBlank(secondPartner)) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) -> 
                cb.or(
                        cb.and(
                                cb.equal(root.get(ChatMessage_.SENDER), firstPartner),
                                cb.equal(root.get(ChatMessage_.RECEIVER), secondPartner)
                        ),
                        cb.and(
                                cb.equal(root.get(ChatMessage_.SENDER), secondPartner),
                                cb.equal(root.get(ChatMessage_.RECEIVER), firstPartner)
                        )
                );
    }
}
