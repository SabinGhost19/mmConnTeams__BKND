package com.sabinghost19.teamslkghostapp.repository;
import com.sabinghost19.teamslkghostapp.model.Message;
import com.sabinghost19.teamslkghostapp.model.Reaction;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
