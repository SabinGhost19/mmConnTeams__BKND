package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EventDTO {
    private UUID id;
    private UUID teamId;
    private UUID channelId;
    private String title;
    private String description;
    private ZonedDateTime eventDate;
    private Integer duration;
    private UUID createdBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Set<EventAttendeeDTO> attendees;
}
