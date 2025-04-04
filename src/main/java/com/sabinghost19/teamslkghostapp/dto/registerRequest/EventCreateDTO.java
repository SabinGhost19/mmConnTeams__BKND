package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDTO {
    private UUID teamId;
    private UUID channelId;
    private String title;
    private String description;
    private String eventDate;
    private Integer duration;
    private UUID createdBy;
}
