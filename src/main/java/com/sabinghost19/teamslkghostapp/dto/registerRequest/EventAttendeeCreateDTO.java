package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAttendeeCreateDTO {
    private UUID eventId;
    private UUID userId;
    private String status;
}
