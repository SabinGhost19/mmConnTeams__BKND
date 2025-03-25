package com.sabinghost19.teamslkghostapp.repository;
import com.sabinghost19.teamslkghostapp.model.Message;
import com.sabinghost19.teamslkghostapp.model.MessageReadStatus;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, UUID> {

        Optional<MessageReadStatus> findByMessageAndUser(Message message, User user);
}
