package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.TicketDTO;
import com.sabinghost19.teamslkghostapp.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;


import com.sabinghost19.teamslkghostapp.model.Ticket;
import com.sabinghost19.teamslkghostapp.repository.TicketRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    /**
     * Creează un nou ticket
     */
    @Transactional
    public Ticket createTicket(TicketDTO ticketDTO) {
        // Verifică dacă utilizatorul există
        if (!userRepository.existsById(ticketDTO.getUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + ticketDTO.getUserId());
        }

        // Verifică dacă sursa (persoana care asignează) există
        if (!userRepository.existsById(ticketDTO.getSourceId())) {
            throw new ResourceNotFoundException("Source user not found with id: " + ticketDTO.getSourceId());
        }

        Ticket ticket = Ticket.builder()
                .userId(ticketDTO.getUserId())
                .sourceId(ticketDTO.getSourceId())
                .title(ticketDTO.getTitle())
                .deadline(ticketDTO.getDeadline())
                .purpose(ticketDTO.getPurpose())
                .description(ticketDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ticketRepository.save(ticket);
    }

    /**
     * Obține toate ticket-urile
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Obține un ticket după ID
     */
    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    /**
     * Obține toate ticket-urile unui utilizator
     */
    public List<Ticket> getTicketsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return ticketRepository.findByUserId(userId);
    }

    /**
     * Obține toate ticket-urile create de o anumită sursă (user)
     */
    public List<Ticket> getTicketsBySourceId(UUID sourceId) {
        if (!userRepository.existsById(sourceId)) {
            throw new ResourceNotFoundException("Source user not found with id: " + sourceId);
        }
        return ticketRepository.findBySourceId(sourceId);
    }

    /**
     * Actualizează un ticket
     */
    @Transactional
    public Ticket updateTicket(UUID id, TicketDTO ticketDTO) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        if (ticketDTO.getTitle() != null) {
            ticket.setTitle(ticketDTO.getTitle());
        }

        if (ticketDTO.getDeadline() != null) {
            ticket.setDeadline(ticketDTO.getDeadline());
        }

        if (ticketDTO.getPurpose() != null) {
            ticket.setPurpose(ticketDTO.getPurpose());
        }

        if (ticketDTO.getDescription() != null) {
            ticket.setDescription(ticketDTO.getDescription());
        }

        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    /**
     * Șterge un ticket
     */
    @Transactional
    public void deleteTicket(UUID id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }
}