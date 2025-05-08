package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.TicketDTO;
import com.sabinghost19.teamslkghostapp.exceptions.ResourceNotFoundException;
import com.sabinghost19.teamslkghostapp.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sabinghost19.teamslkghostapp.model.Ticket;
import com.sabinghost19.teamslkghostapp.repository.TicketRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Transactional
    public Ticket createTicket(TicketDTO ticketDTO,UUID userId) {
        if (!userRepository.existsById(ticketDTO.getUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + ticketDTO.getUserId());
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Source user not found with id: " + ticketDTO.getSourceId());
        }

        if (!channelRepository.existsById(ticketDTO.getChannelId())) {
            throw new ResourceNotFoundException("Channel not found with id: " + ticketDTO.getChannelId());
        }

        Ticket ticket = Ticket.builder()
                .userId(ticketDTO.getUserId())
                .sourceId(userId)
                .channelId(ticketDTO.getChannelId())
                .title(ticketDTO.getTitle())
                .deadline(ticketDTO.getDeadline())
                .purpose(ticketDTO.getPurpose())
                .description(ticketDTO.getDescription())
                .destinationId(ticketDTO.getDestinationId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ticketRepository.save(ticket);
    }


    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }


    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }


    public List<Ticket> getTicketsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return ticketRepository.findByUserId(userId);
    }


    public List<Ticket> getTicketsBySourceId(UUID sourceId) {
        if (!userRepository.existsById(sourceId)) {
            throw new ResourceNotFoundException("Source user not found with id: " + sourceId);
        }
        return ticketRepository.findBySourceId(sourceId);
    }


    public List<Ticket> getTicketsByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResourceNotFoundException("Channel not found with id: " + channelId);
        }
        return ticketRepository.findByChannelId(channelId);
    }

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

        if (ticketDTO.getChannelId() != null) {
            if (!channelRepository.existsById(ticketDTO.getChannelId())) {
                throw new ResourceNotFoundException("Channel not found with id: " + ticketDTO.getChannelId());
            }
            ticket.setChannelId(ticketDTO.getChannelId());
        }

        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }


    @Transactional
    public void deleteTicket(UUID id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }


    @Transactional
    public void deleteTicketsByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResourceNotFoundException("Channel not found with id: " + channelId);
        }
        ticketRepository.deleteByChannelId(channelId);
    }
}