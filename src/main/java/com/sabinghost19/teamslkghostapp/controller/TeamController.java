package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.model.Channel;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.services.ChannelService;
import com.sabinghost19.teamslkghostapp.services.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class TeamController {

    private final TeamService teamService;
    private final ChannelService channelService;
    private final Logger logger= LoggerFactory.getLogger(TeamController.class);

    @Autowired
    public TeamController(TeamService teamService, ChannelService channelService) {
        this.channelService = channelService;
        this.teamService = teamService;
    }

    @GetMapping("/users/team-mates")
    public ResponseEntity<List<TeamUsersMutateDTO>> getUsersInSameTeams(
            @RequestAttribute("userId") UUID userId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        List<TeamUsersMutateDTO> teamMates = teamService.getUsersInSameTeams(currentUser.getId());

        return ResponseEntity.ok(teamMates);
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamDTO>> getUserTeams(@RequestAttribute("userId") UUID userId) {
            List<TeamDTO>teams=teamService.getTeamsForUser(userId);
            if (teams.isEmpty()){
                logger.error("Teams are null....: {}", teams);}
        return ResponseEntity.ok(teams);
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

    @GetMapping("/teams/{teamId}/members/ids")
    public ResponseEntity<List<UUID>> getTeamMembersIDs(@PathVariable UUID teamId) {
        List<UUID> members = teamService.getTeamMembersIDs(teamId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/channels/{channelId}/members")
    public ResponseEntity<?> getChannelMembers(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.getChannelMembers(channelId));
    }

    @GetMapping("/channels/{channelId}/files")
    public ResponseEntity<?> getChannelFiles(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.getChannelFiles(channelId));
    }

    @PostMapping("/channel")
    public ResponseEntity<ChannelDTO> createChannel(@RequestBody ChannelDTO channelDTO,Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        ChannelDTO saved_channel = this.channelService.createChannel(currentUser.getId(),channelDTO);
        return ResponseEntity.ok(saved_channel);
    }

    @PutMapping("/teams/{teamId}/join")
    public ResponseEntity<String> enterTeam(
            @PathVariable UUID teamId,
            Authentication authentication
    ) {
        try {
            System.out.println("Received teamId: " + teamId);
            if (teamId == null) {
                return ResponseEntity.badRequest().body("Team ID is required");
            }
            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok(this.teamService.enterTeam(user, teamId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Team ID format");
        }
    }

    private boolean isValidUUID(String str) {
        String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        System.out.println("VALIDDDDDDDD...????");
        return str.matches(uuidRegex);
    }

    private UUID convertStringToUUID(String teamId) {
        if (!isValidUUID(teamId)) {
            System.out.println("VALIDDDDDDDD...YESSSSSS");
            return UUID.fromString(teamId);
        }
        return UUID.fromString("");
    }

    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(
            @RequestBody TeamDTO teamRequest,
            Authentication authentication
    ) {
        try {
            if (teamRequest == null) {
                return ResponseEntity.badRequest().body("Datele pentru echipă sunt goale");
            }

            User currentUser = (User) authentication.getPrincipal();

            System.out.println("Primite date pentru echipă: " + teamRequest);
            System.out.println("Utilizator curent: " + currentUser.getEmail());

            TeamDTO savedTeam = teamService.createTeam(teamRequest, currentUser);
            return ResponseEntity.ok(savedTeam);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body("Eroare la crearea echipei: " + e.getMessage());
        }
    }
}