package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.EventAttendeeCreateDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.EventCreateDTO;
import com.sabinghost19.teamslkghostapp.model.Event;
import com.sabinghost19.teamslkghostapp.model.EventAttendee;
import com.sabinghost19.teamslkghostapp.repository.EventAttendeeRepository;
import com.sabinghost19.teamslkghostapp.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final EventAttendeeRepository eventAttendeeRepository;

    @Autowired
    public EventService(EventRepository eventRepository, EventAttendeeRepository eventAttendeeRepository) {
        this.eventRepository = eventRepository;
        this.eventAttendeeRepository = eventAttendeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsForUser(UUID userId) {
        List<Event> createdEvents = eventRepository.findByCreatedByWithAttendees(userId);

        List<UUID> eventIds = eventAttendeeRepository.findEventIdByUserId(userId);
        List<Event> attendingEvents = eventRepository.findAllByIdWithAttendees(eventIds);

        Map<UUID, Event> eventMap = new HashMap<>();

        for (Event event : createdEvents) {
            eventMap.put(event.getId(), event);
        }

        for (Event event : attendingEvents) {
            eventMap.put(event.getId(), event);
        }

        List<Event> sortedEvents = new ArrayList<>(eventMap.values());
        sortedEvents.sort(Comparator.comparing(Event::getEventDate));

        return sortedEvents;
    }
    @Transactional
    public Event createEventWithAttendees(EventCreateDTO eventDTO, List<EventAttendeeCreateDTO> attendeesDTO) {
        Event event = new Event();
        event.setTeamId(eventDTO.getTeamId());
        event.setChannelId(eventDTO.getChannelId());
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        ZonedDateTime parsedDate = ZonedDateTime.parse(eventDTO.getEventDate() + "Z");
        event.setEventDate(parsedDate);
        event.setDuration(eventDTO.getDuration());
        event.setCreatedBy(eventDTO.getCreatedBy());

        Event savedEvent = eventRepository.save(event);

        if (attendeesDTO != null && !attendeesDTO.isEmpty()) {
            for (EventAttendeeCreateDTO attendeeDTO : attendeesDTO) {
                EventAttendee attendee = new EventAttendee();
                attendee.setEvent(savedEvent);
                attendee.setUserId(attendeeDTO.getUserId());
                attendee.setStatus(attendeeDTO.getStatus());
                eventAttendeeRepository.save(attendee);
            }
        }

        return savedEvent;
    }
}
