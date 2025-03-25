package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.ChannelDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamDTO;
import com.sabinghost19.teamslkghostapp.services.ChannelService;
import com.sabinghost19.teamslkghostapp.services.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;
    private final ChannelService channelService;


    @GetMapping("/teams")
    public ResponseEntity<List<TeamDTO>> getUserTeams(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(teamService.getTeamsForUser(userId));
    }


    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeamById(teamId));
    }


    @GetMapping("/teams/{teamId}/channels")
    public ResponseEntity<List<ChannelDTO>> getTeamChannels(@PathVariable UUID teamId) {
        return ResponseEntity.ok(channelService.getChannelsForTeam(teamId));
    }


    @GetMapping("/channels/{channelId}")
    public ResponseEntity<ChannelDTO> getChannel(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.getChannelById(channelId));
    }


    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity<?> getTeamMembers(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }


    @GetMapping("/channels/{channelId}/members")
    public ResponseEntity<?> getChannelMembers(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.getChannelMembers(channelId));
    }


    @GetMapping("/channels/{channelId}/files")
    public ResponseEntity<?> getChannelFiles(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.getChannelFiles(channelId));
    }
}