package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.mappers.ReactionMapper;
import com.sabinghost19.teamslkghostapp.model.Channel;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReactionMapper reactionMapper;

    @Transactional
    public void addReaction(ReactionDTO reactionDTO) {
        Message message = messageRepository.findById(reactionDTO.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("Mesaj negăsit"));

        User user = userRepository.findById(reactionDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Utilizator negăsit"));

        reactionRepository
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

    @Transactional
    public void addReaction(ReactionDTO reactionDTO, UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Mesajul cu ID-ul " + messageId + " nu a fost găsit"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilizatorul cu ID-ul " + userId + " nu a fost găsit"));

        Channel channel = message.getChannel();

        Optional<Reaction> existingReaction = reactionRepository.findByMessageIdAndUserIdAndReactionType(
                messageId, userId, reactionDTO.getReactionType());

        if (existingReaction.isPresent()) {
            ReactionDTO dto = reactionMapper.toDTO(existingReaction.get());
            dto.setAction("add");
            return;
        }

        Reaction reaction = Reaction.builder()
                .message(message)
                .user(user)
                .channel(channel)
                .reactionType(reactionDTO.getReactionType())
                .build();

        reaction = reactionRepository.save(reaction);

        ReactionDTO dto = reactionMapper.toDTO(reaction);
        dto.setAction("add");

    }

    @Transactional
    public ReactionDTO removeReaction(UUID messageId, UUID userId, String reactionType) {
        // Găsim reacția
        Reaction reaction = reactionRepository.findByMessageIdAndUserIdAndReactionType(
                        messageId, userId, reactionType)
                .orElseThrow(() -> new EntityNotFoundException("Reacția nu a fost găsită"));

        // Convertim la DTO înainte de ștergere
        ReactionDTO dto = reactionMapper.toDTO(reaction);
        dto.setAction("remove");

        // Ștergem reacția
        reactionRepository.delete(reaction);

        return dto;
    }

    @Transactional
    public List<ReactionDTO> getReactionsForMessage(UUID messageId) {
        List<Reaction> reactions = reactionRepository.findByMessageId(messageId);

        return reactions.stream()
                .map(reactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}


