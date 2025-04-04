package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWithAttendeesDTO {
    private EventCreateDTO event;
    private List<EventAttendeeCreateDTO> attendees;
}
