package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.AttachmentDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.MessageDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.model.Channel;
import com.sabinghost19.teamslkghostapp.model.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sabinghost19.teamslkghostapp.model.MessageReadStatus;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.ChannelRepository;
import com.sabinghost19.teamslkghostapp.repository.MessageReadStatusRepository;
import com.sabinghost19.teamslkghostapp.repository.MessageRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService  {

    private final MessageRepository messageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public MessageDTO saveMessage(MessageDTO messageDTO) {
        // Convertim DTO-ul în entitate

        Channel channel = channelRepository.findById(Long.valueOf(messageDTO.getChannelId()))
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));

        User sender = userRepository.findById(UUID.fromString(String.valueOf(messageDTO.getSenderId())))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Message message = new Message();
        message.setChannel(channel);
        message.setSender(sender);
        message.setContent(messageDTO.getContent());

        LocalDateTime now = LocalDateTime.now();
        message.setCreatedAt(Instant.from(now));
        message.setUpdatedAt(Instant.from(now));


        Message savedMessage = messageRepository.save(message);


        return convertToDTO(savedMessage);
    }

    public List<MessageDTO> getChannelMessages(UUID channelId) {
        List<Message> messages = messageRepository.findByChannel_IdOrderByCreatedAtAsc(channelId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(UUID channelId, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilizator negăsit"));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Canal negăsit"));


        List<Message> unreadMessages = messageRepository.findUnreadMessagesForUser(channelId, userId);

        Instant now = Instant.now();


        List<MessageReadStatus> readStatuses = new ArrayList<>();
        for (Message message : unreadMessages) {

            MessageReadStatus readStatus = messageReadStatusRepository
                    .findByMessageAndUser(message, user)
                    .orElse(MessageReadStatus.builder()
                            .message(message)
                            .user(user)
                            .build());

            readStatus.markAsRead();
            readStatus.setReadAt(now);

            readStatuses.add(readStatus);
        }

        messageReadStatusRepository.saveAll(readStatuses);
    }


    public MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();

        dto.setId(message.getId() != null ? message.getId().hashCode() : null);

        dto.setChannelId(message.getChannel() != null ?
                message.getChannel().getId().hashCode() : null);

        dto.setSenderId(message.getSender() != null ?
                message.getSender().getId().hashCode() : null);

        dto.setContent(message.getContent());

        dto.setCreatedAt(message.getCreatedAt() != null ?
                LocalDateTime.ofInstant(message.getCreatedAt(), ZoneOffset.UTC) : null);

        dto.setUpdatedAt(message.getUpdatedAt() != null ?
                LocalDateTime.ofInstant(message.getUpdatedAt(), ZoneOffset.UTC) : null);

        dto.setAttachments(message.getAttachments().stream()
                .map(attachment -> {
                    AttachmentDTO attachmentDTO = new AttachmentDTO();
                    attachmentDTO.setId(attachment.getId() != null ?
                            attachment.getId().hashCode() : null);
                    attachmentDTO.setFileName(attachment.getFileName());
                    /// DE COMPLETAT

                    return attachmentDTO;
                })
                .collect(Collectors.toList()));

        dto.setReactions(message.getReactions().stream()
                .map(reaction -> {
                    ReactionDTO reactionDTO = new ReactionDTO();
                    reactionDTO.setId(reaction.getId());
                    /// DE COMPLETAT
                    return reactionDTO;
                })
                .collect(Collectors.toList()));


        dto.setIsRead(false); // Sau implementează logica de verificare

        return dto;
    }
}
