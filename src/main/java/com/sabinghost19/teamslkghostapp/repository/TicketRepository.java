package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByUserId(UUID userId);
    List<Ticket> findBySourceId(UUID sourceId);
    List<Ticket> findByDestinationId(UUID destinationId);
    List<Ticket> findByChannelId(UUID channelId);
    void deleteByChannelId(UUID channelId);
    @Modifying
    @Query("DELETE FROM Ticket t WHERE t.deadline IS NOT NULL AND t.deadline < :currentTime")
    void deleteByDeadlineBefore(String currentTime);
}