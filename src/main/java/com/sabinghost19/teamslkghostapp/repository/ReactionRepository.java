package com.sabinghost19.teamslkghostapp.repository;
import com.sabinghost19.teamslkghostapp.model.Message;
import com.sabinghost19.teamslkghostapp.model.Reaction;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Optional<Reaction> findByMessageAndUserAndReactionType(
            Message message,
            User user,
            String reactionType
    );

    void deleteByMessageAndUserAndReactionType(
            Message message,
            User user,
            String reactionType
    );

    List<Reaction> findByMessageId(UUID messageId);
    List<Reaction> findByChannelId(UUID channelId);
    List<Reaction> findByUserId(UUID userId);
    Optional<Reaction> findByMessageIdAndUserIdAndReactionType(
            UUID messageId, UUID userId, String reactionType);
    void deleteByMessageId(UUID messageId);
    long countByMessageIdAndReactionType(UUID messageId, String reactionType);
}
