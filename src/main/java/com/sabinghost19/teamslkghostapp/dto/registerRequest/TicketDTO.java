package com.sabinghost19.teamslkghostapp.dto.registerRequest;


import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private UUID id;
    private UUID userId;
    private UUID sourceId;
    private UUID destinationId;
    private UUID channelId;
    private String title;
    private LocalDateTime deadline;
    private String purpose;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}