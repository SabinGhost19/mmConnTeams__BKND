package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.TicketDTO;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.services.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sabinghost19.teamslkghostapp.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketDTO ticketDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Ticket createdTicket = ticketService.createTicket(ticketDTO, user.getId());
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Ticket> tickets = ticketService.getAllTickets(user.getId());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/source")
    public ResponseEntity<List<Ticket>> getTicketsBySourceId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Ticket> tickets = ticketService.getTicketsBySourceId(user.getId());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/destination")
    public ResponseEntity<List<Ticket>> getTicketsByDestinationId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Ticket> tickets = ticketService.getTicketsByDestinationId(user.getId());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Ticket>> getTicketsByChannelId(@PathVariable UUID channelId) {
        List<Ticket> tickets = ticketService.getTicketsByChannelId(channelId);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable UUID id, @RequestBody TicketDTO ticketDTO) {
        Ticket updatedTicket = ticketService.updateTicket(id, ticketDTO);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/channel/{channelId}")
    public ResponseEntity<Void> deleteTicketsByChannelId(@PathVariable UUID channelId) {
        ticketService.deleteTicketsByChannelId(channelId);
        return ResponseEntity.noContent().build();
    }
}