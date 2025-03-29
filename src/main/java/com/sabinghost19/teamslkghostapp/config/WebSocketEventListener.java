package com.sabinghost19.teamslkghostapp.config;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserStatusDTO;
import com.sabinghost19.teamslkghostapp.enums.Status;
import com.sabinghost19.teamslkghostapp.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    private final Map<String, Integer> sessionUserMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new WebSocket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        //UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
        UUID userId = (UUID) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userId");

        if (userId != null) {
            log.info("User disconnected: {}", userId);

            userService.updateStatus(userId, "OFFLINE");

            UserStatusDTO statusUpdate = new UserStatusDTO(userId, "OFFLINE");
            messagingTemplate.convertAndSend("/topic/user-status", statusUpdate);

            sessionUserMap.remove(sessionId);
        }
    }
}