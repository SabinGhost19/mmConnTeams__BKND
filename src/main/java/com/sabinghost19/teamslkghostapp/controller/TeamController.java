package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.services.ChannelService;
import com.sabinghost19.teamslkghostapp.services.FileService;
import com.sabinghost19.teamslkghostapp.services.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class TeamController {

    private final TeamService teamService;
    private final ChannelService channelService;
    private final Logger logger= LoggerFactory.getLogger(TeamController.class);
    private final FileService fileService;

    @Autowired
    public TeamController(TeamService teamService, ChannelService channelService, FileService fileService) {
        this.channelService = channelService;
        this.teamService = teamService;
        this.fileService=fileService;
    }

    @GetMapping("/users/team-mates")
    public ResponseEntity<List<TeamUsersMutateDTO>> getUsersInSameTeams(
            @RequestAttribute("userId") UUID userId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        List<TeamUsersMutateDTO> teamMates = teamService.getUsersInSameTeams(currentUser.getId());

        return ResponseEntity.ok(teamMates);
    }
    @GetMapping("/events-getall")
    public ResponseEntity<List<EventDTO>>getEvent(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(this.teamService.getEvents(currentUser.getId()));
    }

    @GetMapping("/user-teams")
    public ResponseEntity<List<TeamDTO>> getUserTeamsSimple(Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        List<TeamDTO>teams=teamService.getTeamsForUser(currentUser.getId());
        if (teams.isEmpty()){
            logger.error("Teams are null....: {}", teams);}
        return ResponseEntity.ok(teams);
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
    @GetMapping("/channels-getforuser")
    public ResponseEntity<List<ChannelDTO>> getChannelsForUser(Authentication authentication) {
        User user= (User) authentication.getPrincipal();
        return ResponseEntity.ok(this.teamService.getChannels(user.getId()));
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

    @PostMapping("/teams/upload-image")
    public ResponseEntity<?> uploadTeamImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("teamId") UUID teamId,
            Authentication authentication) {
        try {
            logger.info("Uploading team image!!!!"+teamId);
            if (image.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Fișierul încărcat este gol");
            }
           String blobURL= this.fileService.uploadFile(image,teamId);
            if(this.teamService.assignImage(blobURL,teamId)){
                return ResponseEntity.status(HttpStatus.CREATED).body(blobURL);
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("INTERNAL SERVER ERROR");
            }

        }
     catch (IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Echipa cu id-ul specificat nu a fost găsită");
    } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Eroare la încărcarea imaginii: " + e.getMessage());
    }
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
            return ResponseEntity.ok(savedTeam.getId());
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body("Eroare la crearea echipei: " + e.getMessage());
        }
    }
}