package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.AttachmentDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.MessageDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    public Integer getUnreadMessages(UUID channelId){
        System.out.println("Channel ID: ");
        System.out.println(channelId);
        return 0;
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

    public Integer getNumberOfMessages(){
        return this.messageRepository.findAll().size();
    }

    public Integer getNumberOfMessagesFromLastWhatTime(String timeRange) {
        try {
            Instant now = Instant.now();
            Instant targetTime;

            if (timeRange.matches("\"?\\d+[dh]\"?")) {
                timeRange = timeRange.replace("\"", "");

                int amount = Integer.parseInt(timeRange.substring(0, timeRange.length() - 1));
                char unit = timeRange.charAt(timeRange.length() - 1);

                if (unit == 'd') {
                    targetTime = now.minus(amount, ChronoUnit.DAYS);
                } else {
                    targetTime = now.minus(amount, ChronoUnit.HOURS);
                }
            } else {
                targetTime = Instant.parse(timeRange);
            }

            return (int) messageRepository.findAll().stream()
                    .filter(message -> message.getCreatedAt().isAfter(targetTime))
                    .count();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format. Use ISO-8601 format (e.g., 2025-04-03T10:15:30Z) or periods like '24h', '7d', '30d', '365d'", e);
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
        message.setCreatedAt(now.atZone(ZoneId.systemDefault()).toInstant());
        message.setUpdatedAt(now.atZone(ZoneId.systemDefault()).toInstant());

        Message savedMessage = messageRepository.save(message);

        return convertToDTO(savedMessage);
    }


    @Transactional
    public List<MessageDTO> getChannelMessages(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelIdWithAll(channelId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Integer getMessageCountForTeam(UUID teamId) {
        return (int)this.messageRepository.countByChannelTeamId(teamId);
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

        dto.setId(message.getId() != null ? message.getId() : UUID.randomUUID());

        // Safely access channel ID
        if (message.getChannel() != null) {
            dto.setChannelId(message.getChannel().getId() != null ? message.getChannel().getId() : UUID.randomUUID());
        } else {
            dto.setChannelId(UUID.randomUUID());
        }

        // Safely access sender ID
        if (message.getSender() != null) {
            dto.setSenderId(message.getSender().getId() != null ? message.getSender().getId() : UUID.randomUUID());
        } else {
            dto.setSenderId(UUID.randomUUID());
        }

        dto.setContent(message.getContent());

        dto.setCreatedAt(message.getCreatedAt() != null ?
                LocalDateTime.ofInstant(message.getCreatedAt(), ZoneOffset.UTC) : null);

        dto.setUpdatedAt(message.getUpdatedAt() != null ?
                LocalDateTime.ofInstant(message.getUpdatedAt(), ZoneOffset.UTC) : null);

        // Convert attachments safely
        if (message.getAttachments() != null) {
            dto.setAttachments(message.getAttachments().stream()
                    .filter(Objects::nonNull) // Filter out null attachments
                    .map(attachment -> {
                        FileDTO attachmentDTO = new FileDTO();
                        attachmentDTO.setFileName(attachment.getFileName());

                        // Safely access team ID
                        if (attachment.getTeam() != null) {
                            attachmentDTO.setTeamId(attachment.getTeam().getId());
                        }

                        // Safely access channel ID
                        if (attachment.getChannel() != null) {
                            attachmentDTO.setChannelId(attachment.getChannel().getId());
                        }

                        attachmentDTO.setUploadedAt(attachment.getUploadedAt());
                        attachmentDTO.setFileSize(attachment.getFileSize());

                        // Safely access uploaded by ID
                        if (attachment.getUploadedBy() != null) {
                            attachmentDTO.setUploadedById(attachment.getUploadedBy().getId());
                        }

                        attachmentDTO.setUrl(attachment.getUrl());
                        attachmentDTO.setFileType(attachment.getFileType());
                        attachmentDTO.setAwsS3Key(attachment.getAwsS3Key());
                        return attachmentDTO;
                    })
                    .collect(Collectors.toList()));
        } else {
            dto.setAttachments(new ArrayList<>());
        }

        // Convert reactions safely
        if (message.getReactions() != null) {
            dto.setReactions(message.getReactions().stream()
                    .filter(Objects::nonNull) // Filter out null reactions
                    .map(reaction -> {
                        ReactionDTO reactionDTO = new ReactionDTO();
                        reactionDTO.setId(reaction.getId());

                        // Safely access message ID
                        if (reaction.getMessage() != null) {
                            reactionDTO.setMessageId(reaction.getMessage().getId());
                        } else {
                            reactionDTO.setMessageId(message.getId()); // Use the current message ID
                        }

                        // Safely access user ID
                        if (reaction.getUser() != null) {
                            reactionDTO.setUserId(reaction.getUser().getId());
                        }

                        // Safely access channel ID
                        if (reaction.getChannel() != null) {
                            reactionDTO.setChannelId(reaction.getChannel().getId());
                        } else if (message.getChannel() != null) {
                            reactionDTO.setChannelId(message.getChannel().getId()); // Use message's channel ID
                        }

                        reactionDTO.setReactionType(reaction.getReactionType());
                        reactionDTO.setAction("add");
                        return reactionDTO;
                    })
                    .collect(Collectors.toList()));
        } else {
            dto.setReactions(new ArrayList<>());
        }

        dto.setIsRead(false);

        return dto;
    }
}
