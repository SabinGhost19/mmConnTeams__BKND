package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventAttendeeRepository extends JpaRepository<EventAttendee, UUID> {

    @Query("SELECT ea.event.id FROM EventAttendee ea WHERE ea.userId = :userId")
    List<UUID> findEventIdByUserId(@Param("userId") UUID userId);

}
