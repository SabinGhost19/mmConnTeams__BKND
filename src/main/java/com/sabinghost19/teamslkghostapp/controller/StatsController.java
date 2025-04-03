package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.services.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stat")
@Slf4j
public class StatsController {

    private final Logger logger=  LoggerFactory.getLogger(StatsController.class);
    private final UserService userService;
    private final TeamService teamService;
    private final ChannelService  channelService;
    private final MessageService messageService;
    private final RefreshTokenService refreshTokenService;
    private final ReactionService reactionService;
    private final FileService fileService;

    @Autowired
    public StatsController(UserService userService,
                           TeamService teamService,
                           ChannelService channelService,
                           MessageService messageService,
                           RefreshTokenService refreshTokenService,
                           ReactionService reactionService,
                           FileService fileService) {
        this.userService = userService;
        this.teamService = teamService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.refreshTokenService = refreshTokenService;
        this.reactionService = reactionService;
        this.fileService = fileService;
    }

    @GetMapping("/users")
    public List<TeamUsersMutateDTO> getAllUsers(){
       return this.userService.getAllTeamsUsers();
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamDetailsDTO>> getAllTeams() {

        List<TeamDTO> allTeamsDTO = this.teamService.getAllTeams();
        List<TeamDetailsDTO> teamDetailsDTOs = new ArrayList<>();


        for (TeamDTO teamDTO : allTeamsDTO) {
            UUID teamId = teamDTO.getId();

            Integer channelCount = this.channelService.getChannelCountForTeam(teamId);

            Integer messageCount = this.messageService.getMessageCountForTeam(teamId);

            Integer reactionCount = this.reactionService.getReactionCountForTeam(teamId);

            Team team = teamService.findTeambyId(teamId);

            TeamDetailsDTO detailsDTO = new TeamDetailsDTO();
            teamDetailsDTOs.add(detailsDTO.toDto(team, messageCount, reactionCount, channelCount));
        }

        return ResponseEntity.ok(teamDetailsDTOs);
    }

    @GetMapping("/{teamID}/channels")
    public ResponseEntity<List<ChannelDTO>>getAllChannels(@PathVariable UUID teamID){
        return ResponseEntity.ok(channelService.getChannelsForTeam(teamID));
    }

    @PostMapping("/numberOfMessages/past")
    public ResponseEntity<Integer> getNumberOfMessages(@RequestBody String messageTime){
        return ResponseEntity.ok().body(this.messageService.getNumberOfMessagesFromLastWhatTime(messageTime));
    }

    @GetMapping("/numberOfMessages")
    public ResponseEntity<Integer>getNumberOfMessages(){
        return ResponseEntity.ok().body(this.messageService.getNumberOfMessages());
    }

    @GetMapping("/NumberRecentUsers")
    public ResponseEntity<Integer>getNumberOfRecentUsers(){
        return ResponseEntity.ok().body(this.refreshTokenService.getNumberOfRecentStoredTokens());
    }

    @GetMapping("/files")
    public ResponseEntity<Integer>getNumberOfFiles(){
        return ResponseEntity.ok().body(this.fileService.getNumberOfFiles());
    }

    @GetMapping("/reactions")
    public ResponseEntity<Integer>getNumberOfReactions(){
        return ResponseEntity.ok().body(this.reactionService.getNumberOfReactions());
    }

    @PostMapping("/reactions")
    public ResponseEntity<Integer>getNumberOfReactionsByType(@RequestBody ReactionTypeDTO type){
        return ResponseEntity.ok().body(this.reactionService.getNumberOfReactionsByType(type.getType()));
    }










}
