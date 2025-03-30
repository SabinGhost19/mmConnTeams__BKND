package com.sabinghost19.teamslkghostapp.controller;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.MessageDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.services.MessageService;
import com.sabinghost19.teamslkghostapp.services.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class MessageController {

    private final MessageService messageService;
    private final ReactionService reactionService;

    @Autowired
    public MessageController(MessageService messageService, ReactionService reactionService) {
        this.messageService = messageService;
        this.reactionService = reactionService;
    }

    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<List<MessageDTO>> getChannelMessages(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.getChannelMessages(channelId));
    }

    @GetMapping("/channels/{channelId}/unread-count")
    public ResponseEntity<Integer> getChannelMessagesUnreadCount(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.getUnreadMessages(channelId));
    }


    @GetMapping("/channels/{channelId}/messages/after/{timestamp}")
    public ResponseEntity<List<MessageDTO>> getNewMessages(
            @PathVariable UUID channelId,
            @PathVariable Long timestamp) {

        List<MessageDTO> newMessages = messageService.getMessagesAfterTimestamp(channelId, Instant.ofEpochSecond(timestamp));
        return ResponseEntity.ok(newMessages);
    }


    @PostMapping("/messages")
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) {
        MessageDTO savedMessage = messageService.saveMessage(messageDTO);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable UUID messageId) {

        return ResponseEntity.ok(messageService.getMessage(messageId));
    }


    @PostMapping("/channels/{channelId}/read")
    public ResponseEntity<?> markChannelAsRead(
            @PathVariable  UUID channelId,
            @RequestAttribute("userId") UUID userId) {
        messageService.markMessagesAsRead(channelId, userId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/messages/{messageId}/reactions")
    public ResponseEntity<?> addReaction(
            @PathVariable UUID messageId,
            @RequestBody Map<String, Object> payload) {

        ReactionDTO reactionDTO = new ReactionDTO();
        reactionDTO.setMessageId(messageId);
        reactionDTO.setUserId((UUID) payload.get("user_id"));
        reactionDTO.setReactionType((String) payload.get("reaction_type"));
        reactionDTO.setAction("add");

        reactionService.addReaction(reactionDTO);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/messages/{messageId}/reactions")
    public ResponseEntity<?> removeReaction(
            @PathVariable UUID messageId,
            @RequestBody Map<String, Object> payload) {

        ReactionDTO reactionDTO = new ReactionDTO();
        reactionDTO.setMessageId(messageId);
        reactionDTO.setUserId((UUID)payload.get("user_id"));
        reactionDTO.setReactionType((String) payload.get("reaction_type"));
        reactionDTO.setAction("remove");

        reactionService.removeReaction(reactionDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * Asociază un atașament la un mesaj
     */
    @PostMapping("/messages/{messageId}/attachments")
    public ResponseEntity<?> attachToMessage(
            @PathVariable UUID messageId,
            @RequestBody Map<String, Object> payload) {

        UUID attachmentId = (UUID) payload.get("attachment_id");
        messageService.addAttachmentToMessage(messageId, attachmentId);

        return ResponseEntity.ok().build();
    }
}

