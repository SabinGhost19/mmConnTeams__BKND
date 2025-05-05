package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByCreatedBy(UUID userId);

    @Query("SELECT e FROM Event e WHERE e.eventDate < :currentTime")
    List<Event> findEventsBeforeDate(@Param("currentTime") ZonedDateTime currentTime);


    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.attendees WHERE e.createdBy = :userId")
    List<Event> findByCreatedByWithAttendees(@Param("userId") UUID userId);

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.attendees WHERE e.id IN :eventIds")
    List<Event> findAllByIdWithAttendees(@Param("eventIds") List<UUID> eventIds);


}
