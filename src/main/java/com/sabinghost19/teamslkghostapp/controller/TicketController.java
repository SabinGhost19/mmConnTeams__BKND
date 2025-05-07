package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.TicketDTO;
import com.sabinghost19.teamslkghostapp.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketDTO ticketDTO) {
        Ticket createdTicket = ticketService.createTicket(ticketDTO);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getTicketsByUserId(@PathVariable UUID userId) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/source/{sourceId}")
    public ResponseEntity<List<Ticket>> getTicketsBySourceId(@PathVariable UUID sourceId) {
        List<Ticket> tickets = ticketService.getTicketsBySourceId(sourceId);
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
}
