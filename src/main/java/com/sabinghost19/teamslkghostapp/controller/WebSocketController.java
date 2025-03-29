package com.sabinghost19.teamslkghostapp.controller;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.MessageDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TypingDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserStatusDTO;
import com.sabinghost19.teamslkghostapp.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.sabinghost19.teamslkghostapp.services.MessageService;
import com.sabinghost19.teamslkghostapp.services.ReactionService;
import com.sabinghost19.teamslkghostapp.services.UserService;
@Controller
@Slf4j
public class WebSocketController {
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userChannelsMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> channelSubscriptionsMap = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate,
                               UserService userService,
                               MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.messageService = messageService;
    }

    @MessageMapping("/authenticate")
    public void authenticate(@Payload Map<String, Object> payload,
                             SimpMessageHeaderAccessor headerAccessor) {
        String userIdStr = (String) payload.get("userId");
        if (userIdStr != null && headerAccessor.getSessionId() != null) {
            try {
                UUID userId = UUID.fromString(userIdStr);
                sessionUserMap.put(headerAccessor.getSessionId(), userIdStr);

                userService.updateStatus(userId, "ONLINE");

                messagingTemplate.convertAndSend("/topic/user-status",
                        Map.of("userId", userIdStr, "status", "ONLINE"));
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format: {}", userIdStr);
            }
        }
    }

    @MessageMapping("/join-channel")
    public void joinChannel(@Payload String channelId,
                            SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String userId = sessionUserMap.get(sessionId);

        if (userId != null && channelId != null) {
            userChannelsMap.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                    .add(channelId);

            channelSubscriptionsMap.computeIfAbsent(channelId, k -> ConcurrentHashMap.newKeySet())
                    .add(sessionId);

            log.info("User {} joined channel {}", userId, channelId);
        }
    }

    @MessageMapping("/send-message")
    public void sendMessage(@Payload Map<String, Object> messageData,
                            SimpMessageHeaderAccessor headerAccessor) {
        String userIdStr = sessionUserMap.get(headerAccessor.getSessionId());
        String channelIdStr = (String) messageData.get("channelId");

        if (userIdStr != null && channelIdStr != null) {
            try {
                UUID userId = UUID.fromString(userIdStr);
                UUID channelId = UUID.fromString(channelIdStr);

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setSenderId(userId);
                messageDTO.setChannelId(channelId);
                messageDTO.setContent((String) messageData.get("content"));

                MessageDTO savedMessage = messageService.saveMessage(messageDTO);


                Map<String, Object> response = new HashMap<>();
                response.put("id", savedMessage.getId().toString());
                response.put("senderId", savedMessage.getSenderId().toString());
                response.put("channelId", savedMessage.getChannelId().toString());
                response.put("content", savedMessage.getContent());

                messagingTemplate.convertAndSend("/topic/channel/" + channelIdStr, response);
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format: userId={}, channelId={}", userIdStr, channelIdStr);
            }
        }
    }

    @MessageMapping("/focus-channel")
    public void focusChannel(@Payload String channelIdStr,
                             SimpMessageHeaderAccessor headerAccessor) {
        String userIdStr = sessionUserMap.get(headerAccessor.getSessionId());

        if (userIdStr != null && channelIdStr != null) {
            try {
                UUID userId = UUID.fromString(userIdStr);
                UUID channelId = UUID.fromString(channelIdStr);

                messageService.markMessagesAsRead(channelId, userId);

                messagingTemplate.convertAndSend("/topic/channel/" + channelIdStr + "/read-status",
                        Map.of(
                                "userId", userIdStr,
                                "channelId", channelIdStr,
                                "unreadCount", 0
                        ));
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format: userId={}, channelId={}", userIdStr, channelIdStr);
            }
        }
    }
}