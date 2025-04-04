package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDTO {
    private String title;
    private String description;
    private ZonedDateTime eventDate;
    private Integer duration;
}