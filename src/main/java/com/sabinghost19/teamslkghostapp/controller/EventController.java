package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.model.Event;
import com.sabinghost19.teamslkghostapp.model.EventAttendee;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EventController {

    private final Logger logger=  LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    @Transactional(readOnly = true)
    public ResponseEntity<List<EventDTO>> getAllEvents(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UUID userId = user.getId();

        List<Event> events = eventService.getEventsForUser(userId);
        List<EventDTO> eventDTOs = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs);
    }

    private EventDTO convertToDTO(Event event) {

        Set<EventAttendeeDTO> attendeeDTOs = new HashSet<>();
        if (event.getAttendees() != null) {
            for (EventAttendee attendee : event.getAttendees()) {
                EventAttendeeDTO attendeeDTO = new EventAttendeeDTO(
                        attendee.getId(),
                        attendee.getEvent().getId(),
                        attendee.getUserId(),
                        attendee.getStatus(),
                        attendee.getCreatedAt()
                );
                attendeeDTOs.add(attendeeDTO);
            }
        }
        return new EventDTO(
                event.getId(),
                event.getTeamId(),
                event.getChannelId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getDuration(),
                event.getCreatedBy(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                attendeeDTOs
        );
    }

    @PostMapping("/events-add")
    public ResponseEntity<Event> addEvent(@RequestBody EventWithAttendeesDTO eventWithAttendeesDTO) {
        logger.info("Creating event with data: {}", eventWithAttendeesDTO);

        EventCreateDTO eventDTO = eventWithAttendeesDTO.getEvent();
        List<EventAttendeeCreateDTO> attendeesDTO = eventWithAttendeesDTO.getAttendees();

       Event createdEvent = eventService.createEventWithAttendees(eventDTO, attendeesDTO);
        return ResponseEntity.ok().body(createdEvent);
    }

    @PutMapping("/events-update/{eventID}")
    public ResponseEntity<EventUpdateDTO>addEvent(@RequestBody EventUpdateDTO eventUpdateDTO,
                                                  @PathVariable String eventID){
        logger.info("addEvent is: ",eventUpdateDTO.toString());
        return ResponseEntity.ok().body(eventUpdateDTO);
    }

    @DeleteMapping("/events-delete/{eventID}")
    public ResponseEntity<Integer> addEvent(@PathVariable String eventID){
        logger.info("addEvent is: ",eventID);
        return ResponseEntity.ok().body(0);
    }



}
