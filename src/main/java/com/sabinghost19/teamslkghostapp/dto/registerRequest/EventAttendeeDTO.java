package com.sabinghost19.teamslkghostapp.dto.registerRequest;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAttendeeDTO {
    private UUID id;
    private UUID eventId;
    private UUID userId;
    private String status;
    private ZonedDateTime createdAt;
}
