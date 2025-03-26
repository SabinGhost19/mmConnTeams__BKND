package com.sabinghost19.teamslkghostapp.controller;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.MessageDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TypingDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
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

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ReactionService reactionService;
    private final UserService userService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, MessageService messageService, ReactionService reactionService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.reactionService = reactionService;
        this.userService = userService;
    }

    // Maparea sesiunilor la utilizatori
    private final Map<String, Integer> sessionUserMap = new ConcurrentHashMap<>();

    // Maparea canalelor active per sesiune
    private final Map<String, Set<Integer>> userChannelsMap = new ConcurrentHashMap<>();

    /**
     * Autentificarea utilizatorului via WebSocket
     */
    @MessageMapping("/authenticate")
    public void authenticate(@Payload Map<String, Object> payload,
                             SimpMessageHeaderAccessor headerAccessor) {
        Integer userId = (Integer) payload.get("userId");
        if (userId != null && headerAccessor.getSessionId() != null) {
            log.info("User {} authenticated via WebSocket", userId);

            // Salvează ID-ul utilizatorului în sesiune
            headerAccessor.getSessionAttributes().put("userId", userId);
            sessionUserMap.put(headerAccessor.getSessionId(), userId);

            // Actualizează statusul utilizatorului
            userService.updateStatus(UUID.fromString(userId.toString()), "online");

            // Notifică toți utilizatorii despre schimbarea de status
            UserStatusDTO statusUpdate = new UserStatusDTO(UUID.fromString(userId.toString()), "online");
            messagingTemplate.convertAndSend("/topic/user-status", statusUpdate);
        }
    }

    /**
     * Utilizatorul se alătură unui canal
     */
    @MessageMapping("/join-channel")
    public void joinChannel(@Payload Integer channelId,
                            SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Integer userId = sessionUserMap.get(sessionId);

        if (userId != null) {
            log.info("User {} joined channel {}", userId, channelId);

            // Adaugă canalul la lista de canale ale utilizatorului
            userChannelsMap.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                    .add(channelId);

            // Verifică dacă utilizatorul are permisiunea de a accesa acest canal
            // (acest cod ar trebui implementat în funcție de logica aplicației)
        }
    }

    /**
     * Utilizatorul părăsește un canal
     */
    @MessageMapping("/leave-channel")
    public void leaveChannel(@Payload Integer channelId,
                             SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        if (sessionId != null && userChannelsMap.containsKey(sessionId)) {
            userChannelsMap.get(sessionId).remove(channelId);
            log.info("User left channel {}", channelId);
        }
    }

    /**
     * Utilizatorul se focusează pe un canal (pentru a marca mesajele ca citite)
     */
    @MessageMapping("/focus-channel")
    public void focusChannel(@Payload UUID channelId,
                             SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Integer userInteger = sessionUserMap.get(sessionId);
        UUID userId = UUID.fromString(userInteger.toString());

        if (userId != null) {
            log.info("User {} focused on channel {}", userId, channelId);

            // Marchează mesajele necitite ca fiind citite
            messageService.markMessagesAsRead(channelId, userId);

            // Actualizează contorul de necitite pentru frontend
            messagingTemplate.convertAndSend(
                    "/topic/channel/" + channelId + "/read-status",
                    Map.of("userId", userId, "channelId", channelId, "unreadCount", 0)
            );
        }
    }

    /**
     * Utilizatorul părăsește focusul de pe un canal
     */
    @MessageMapping("/unfocus-channel")
    public void unfocusChannel(@Payload Integer channelId,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Opțional - poate fi folosit pentru a marca momentul când utilizatorul
        // nu mai este activ într-un canal
        log.info("User unfocused from channel {}", channelId);
    }

    /**
     * Trimite un mesaj nou
     */
    @MessageMapping("/send-message")
    public void sendMessage(@Payload MessageDTO messageDTO,
                            SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Integer userId = sessionUserMap.get(sessionId);

        if (userId != null && userId.equals(messageDTO.getSenderId())) {
            log.info("Received message for channel {}", messageDTO.getChannelId());

            // Salvează mesajul în baza de date
            MessageDTO savedMessage = messageService.saveMessage(messageDTO);

            // Transmite mesajul tuturor utilizatorilor abonați la acest canal
            messagingTemplate.convertAndSend("/topic/channel/" + messageDTO.getChannelId(), savedMessage);
        }
    }

    /**
     * Gestionează reacțiile la mesaje
     */
    @MessageMapping("/message-reaction")
    public void handleReaction(@Payload ReactionDTO reactionDTO,
                               SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Integer userId = sessionUserMap.get(sessionId);

        if (userId != null && userId.equals(reactionDTO.getUserId())) {
            log.info("Received reaction {} for message {}", reactionDTO.getReactionType(), reactionDTO.getMessageId());

            // Procesează reacția (adaugă sau elimină)
            if ("add".equals(reactionDTO.getAction())) {
                reactionService.addReaction(reactionDTO);
            } else if ("remove".equals(reactionDTO.getAction())) {
                reactionService.removeReaction(reactionDTO);
            }

            // Transmite actualizarea reacției tuturor utilizatorilor din canal
            messagingTemplate.convertAndSend("/topic/channel/" + reactionDTO.getChannelId(), reactionDTO);
        }
    }

    /**
     * Gestionează indicatorii de typing
     */
    @MessageMapping("/typing-indicator")
    public void handleTypingIndicator(@Payload TypingDTO typingDTO,
                                      SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Integer userId = sessionUserMap.get(sessionId);

        if (userId != null && userId.equals(typingDTO.getUserId())) {
            // Transmite indicatorul de typing către toți utilizatorii din canal
            messagingTemplate.convertAndSend(
                    "/topic/channel/" + typingDTO.getChannelId() + "/typing",
                    typingDTO
            );
        }
    }

    /**
     * Actualizează statusul utilizatorului
     */
    @MessageMapping("/update-status")
    public void updateStatus(@Payload UserStatusDTO statusDTO,
                             SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Integer userInteger = sessionUserMap.get(sessionId);
        UUID userId = UUID.fromString(userInteger.toString());

        if (userId != null && userId.equals(statusDTO.getUserId())) {
            log.info("Updating status for user {} to {}", userId, statusDTO.getStatus());

            // Actualizează statusul în baza de date
            userService.updateStatus(userId, statusDTO.getStatus());

            // Notifică toți utilizatorii despre schimbarea de status
            messagingTemplate.convertAndSend("/topic/user-status", statusDTO);
        }
    }
}

