package com.sabinghost19.teamslkghostapp.repository;
import com.sabinghost19.teamslkghostapp.model.Message;
import com.sabinghost19.teamslkghostapp.model.Reaction;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COUNT(r) FROM Reaction r JOIN r.channel c WHERE c.team.id = :teamId")
    Integer countByChannelTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.reactionType = :type")
    Integer countByReactionType(@Param("type") String type);

    void deleteByMessageAndUserAndReactionType(
            Message message,
            User user,
            String reactionType
    );

    @Query("SELECT r FROM Reaction r JOIN FETCH r.message m JOIN FETCH m.channel WHERE m.id = :messageId")
    List<Reaction> findByMessageId(@Param("messageId") UUID messageId);
    List<Reaction> findByChannelId(UUID channelId);
    List<Reaction> findByUserId(UUID userId);
    Optional<Reaction> findByMessageIdAndUserIdAndReactionType(
            UUID messageId, UUID userId, String reactionType);
    void deleteByMessageId(UUID messageId);
    long countByMessageIdAndReactionType(UUID messageId, String reactionType);
}
