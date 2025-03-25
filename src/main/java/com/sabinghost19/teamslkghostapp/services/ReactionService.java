package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.model.Message;
import com.sabinghost19.teamslkghostapp.model.Reaction;
import com.sabinghost19.teamslkghostapp.model.User;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.repository.MessageRepository;
import com.sabinghost19.teamslkghostapp.repository.ReactionRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reaction addReaction(ReactionDTO reactionDTO) {
        Message message = messageRepository.findById(reactionDTO.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("Mesaj negăsit"));

        User user = userRepository.findById(reactionDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Utilizator negăsit"));

        return reactionRepository
                .findByMessageAndUserAndReactionType(message, user, reactionDTO.getReactionType())
                .orElseGet(() -> {
                    Reaction reaction = new Reaction();
                    reaction.setMessage(message);
                    reaction.setUser(user);
                    reaction.setReactionType(reactionDTO.getReactionType());

                    return reactionRepository.save(reaction);
                });
    }

    @Transactional
    public void removeReaction(ReactionDTO reactionDTO) {
        Message message = messageRepository.findById(reactionDTO.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("Mesaj negăsit"));

        User user = userRepository.findById(reactionDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Utilizator negăsit"));

        reactionRepository.deleteByMessageAndUserAndReactionType(message, user, reactionDTO.getReactionType());
    }
}

