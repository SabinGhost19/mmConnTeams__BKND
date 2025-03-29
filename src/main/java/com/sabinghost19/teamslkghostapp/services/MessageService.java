package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.AttachmentDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.MessageDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sabinghost19.teamslkghostapp.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//@Service
//public class MessageService {
//    public MessageDTO saveMessage(MessageDTO messageDTO) {
//        Message message = new Message();
//        message.setId(UUID.randomUUID().toString()); // Generare UUID string
//        message.setSenderId(messageDTO.getSenderId());
//        message.setChannelId(messageDTO.getChannelId());
//        message.setContent(messageDTO.getContent());
//
//        Message saved = messageRepository.save(message);
//        return mapToDTO(saved);
//    }
//
//    private MessageDTO mapToDTO(Message message) {
//        MessageDTO dto = new MessageDTO();
//        dto.setId(message.getId());
//        dto.setSenderId(message.getSenderId());
//        dto.setChannelId(message.getChannelId());
//        dto.setContent(message.getContent());
//        dto.setCreatedAt(message.getCreatedAt());
//        return dto;
//    }
//}
//public interface MessageRepository extends JpaRepository<Message, String> {
//    List<Message> findByChannelId(String channelId);
//}
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService  {

    private final MessageRepository messageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final FileRepository fileRepository;

    @Autowired
    public MessageService(UserRepository userRepository,
                          ChannelRepository channelRepository,
                          MessageRepository messageRepository,
                          MessageReadStatusRepository  messageReadStatusRepository,
                          FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.messageReadStatusRepository=messageReadStatusRepository;
        this.fileRepository = fileRepository;
    }

    public MessageDTO getMessage(UUID id) {

        Optional<Message> messageOptional = messageRepository.findById(id);

        if (messageOptional.isPresent()) {
            return convertToDTO(messageOptional.get());
        } else {
            throw new EntityNotFoundException("Mesajul cu ID-ul " + id + " nu a fost găsit");
          //or return null...maybe not, IDK, not now
        }
    }

    @Transactional
    public MessageDTO saveMessage(MessageDTO messageDTO) {

        Channel channel = channelRepository.findById(messageDTO.getChannelId())
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

    public List<MessageDTO> getMessagesAfterTimestamp(UUID channelId, Instant timestamp) {

        List<Message> messages = messageRepository.findMessagesAfterTimestamp(channelId, timestamp);

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public void addAttachmentToMessage(UUID messageId, UUID attachmentId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Mesajul cu ID-ul " + messageId + " nu a fost găsit"));

        File file = fileRepository.findById(attachmentId)
                .orElseThrow(() -> new EntityNotFoundException("Fișierul cu ID-ul " + attachmentId + " nu a fost găsit"));
       
        file.setMessage(message);

        message.getAttachments().add(file);

        messageRepository.save(message);
    }

    public MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();

        dto.setId(message.getId()!=null?message.getId():UUID.randomUUID());

        dto.setChannelId(message.getChannel().getId()!=null?message.getChannel().getId():UUID.randomUUID());

        dto.setSenderId(message.getSender().getId()!=null?message.getSender().getId():UUID.randomUUID());

        dto.setContent(message.getContent());

        dto.setCreatedAt(message.getCreatedAt() != null ?
                LocalDateTime.ofInstant(message.getCreatedAt(), ZoneOffset.UTC) : null);

        dto.setUpdatedAt(message.getUpdatedAt() != null ?
                LocalDateTime.ofInstant(message.getUpdatedAt(), ZoneOffset.UTC) : null);

        dto.setAttachments(message.getAttachments().stream()
                .map(attachment -> {
                    FileDTO attachmentDTO = new FileDTO();
                    attachmentDTO.setFileName(attachment.getFileName());
                    attachmentDTO.setTeamId(attachment.getTeam().getId());
                    attachmentDTO.setChannelId(attachment.getChannel().getId());
                    attachmentDTO.setUploadedAt(attachment.getUploadedAt());
                    attachmentDTO.setFileSize(attachment.getFileSize());
                    attachmentDTO.setUploadedById(attachment.getUploadedBy().getId());
                    attachmentDTO.setUrl(attachment.getUrl());
                    attachmentDTO.setFileType(attachment.getFileType());
                    attachmentDTO.setAwsS3Key(attachment.getAwsS3Key());
                    return attachmentDTO;
                })
                .collect(Collectors.toList()));


        dto.setReactions(message.getReactions().stream()
                .map(reaction -> {
                    ReactionDTO reactionDTO = new ReactionDTO();
                    reactionDTO.setId(reaction.getId());
                    reactionDTO.setMessageId(reaction.getMessage().getId());
                    reactionDTO.setUserId(reaction.getUser().getId());
                    reactionDTO.setChannelId(reaction.getChannel().getId());
                    reactionDTO.setReactionType(reaction.getReactionType());
                    //null....???
                    reactionDTO.setAction("");
                    return reactionDTO;
                })
                .collect(Collectors.toList()));

        dto.setIsRead(false);

        return dto;
    }
}
