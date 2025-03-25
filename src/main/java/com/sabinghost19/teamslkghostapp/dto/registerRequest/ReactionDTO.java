package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionDTO {
    private UUID id;
    private UUID messageId;
    private UUID userId;
    private UUID channelId;  // for WebSocket
    private String reactionType;
    private String action;      // add or remove
}