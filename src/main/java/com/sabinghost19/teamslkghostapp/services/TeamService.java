package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.model.*;
import com.sabinghost19.teamslkghostapp.repository.TeamMemberRepository;
import com.sabinghost19.teamslkghostapp.repository.TeamRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public TeamService( UserService userService,TeamRepository teamRepository, UserRepository userRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public boolean assignImage(String blobUrl, UUID teamId) {
        Optional<Team> optionalTeam = this.teamRepository.findById(teamId);

        if (optionalTeam.isPresent()) {
            Team team = optionalTeam.get();
            team.setIconUrl(blobUrl);
            this.teamRepository.save(team);
            return true;
        }

        return false;
    }

    public List<Team> getAllTeams_raw_byUserId(UUID userId) {
        return  this.teamRepository.findTeamsByUserId(userId);
    }
    public List<TeamDTO> getAllTeams(){
        List<Team> teams= this.teamRepository.findAll();
        return teams.stream().map(team->new TeamDTO().toDto(team)).collect(Collectors.toList());
    }

    @Transactional
    public List<ChannelDTO>getChannels(UUID user_id){
        List<Team>teams=this.getAllTeams_raw_byUserId(user_id);
        List<ChannelDTO> allChannels = teams.stream()
                .flatMap(team -> team.getChannels().stream())
                .map(channel -> convertToDTO(channel))
                .collect(Collectors.toList());
        return allChannels;
    }
    public List<EventDTO>getEvents(UUID user_id){
        List<Event> events = teamRepository.findAllEventsByUserId(user_id);

        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<TeamUsersMutateDTO> getUsersInSameTeams(UUID userId) {
        List<User> teamMates = userRepository.findUsersInTeamsWithProfileAndRoles(
                teamMemberRepository.findTeamIdsByUserId(userId),
                userId
        );
        return teamMates.stream()
                .map(this::mapToTeamUsersMutateDto)
                .collect(Collectors.toList());
    }
    private TeamUsersMutateDTO mapToTeamUsersMutateDto(User user) {
        UserProfile profile = user.getProfile();

        return TeamUsersMutateDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .status(user.getStatus())
                .profileImage(profile != null ? profile.getProfileImageUrl() : null)
                .department(profile != null ? profile.getSpecialization() : "N/A")
                .roles(user.getRoles() != null ? user.getRoles() : List.of())
                .build();
    }

    public String enterTeam(User user, UUID teamId) {

        boolean isMemberAlready = teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId());

        if (isMemberAlready) {
            return "Utilizatorul este deja membru al echipei";
        }

        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isEmpty()) {
            return "Team Not Found";
        }

        Team team = teamOptional.get();
        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .role("member")
                .build();

        teamMemberRepository.save(teamMember);
        return "New Team Member Added";
    }
    public TeamDTO createTeam(TeamDTO teamDTO, User creator) {

        Team team = Team.builder()
                .name(teamDTO.getName())
                .description(teamDTO.getDescription())
                .iconUrl(teamDTO.getIconUrl())
                .build();


        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(creator)
                .role("admin")
                .build();

        team.addMember(teamMember);

        Team savedTeam = teamRepository.save(team);

        return TeamDTO.builder()
                .id(savedTeam.getId())
                .name(savedTeam.getName())
                .description(savedTeam.getDescription())
                .iconUrl(savedTeam.getIconUrl())
                .build();
    }
    public List<TeamDTO> getTeamsForUser(UUID userId) {
        return teamRepository.findTeamsByUserId(userId).stream()
                .map(team -> TeamDTO.builder()
                        .id(team.getId())
                        .name(team.getName())
                        .iconUrl(team.getIconUrl())
                        .description(team.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
    public Team findTeambyId(UUID teamId) {
        return this.teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team Not Found"));
    }

    public TeamDTO getTeamById(UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team Not Found"));

        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }

    public List<TeamUsersMutateDTO> getTeamMembers(UUID teamId) {
        return userRepository.findUsersByTeamId(teamId).stream()
                .map(this::mapToTeamUsersMutateDto)
                .collect(Collectors.toList());
    }

    public List<UUID> getTeamMembersIDs(UUID teamId) {
        return userRepository.findUsersByTeamId(teamId).stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public List<TeamUsersMutateDTO> getAllUsersByTeamId(UUID teamId) {
        List<TeamMember> teamMembers = teamMemberRepository.findTeamMembersWithUsersByTeamId(teamId);

        return teamMembers.stream()
                .map(tm -> {
                    User user = tm.getUser();
                    UserProfile profile = user.getProfile();
                    return TeamUsersMutateDTO.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .status(user.getStatus())
                            .profileImage(profile != null ? profile.getProfileImageUrl() : null)
                            .department(profile != null ? profile.getSpecialization() : null)
                            .roles(user.getRoles())
                            .build();
                })
                .collect(Collectors.toList());
    }
    private EventDTO convertToDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .duration(event.getDuration())
                .teamId(event.getTeamId())
                .channelId(event.getChannelId())
                .createdBy(event.getCreatedBy())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
    private ChannelDTO convertToDTO(Channel channel) {
        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .teamId(channel.getTeam().getId())
                .description(channel.getDescription())
                .isPrivate(channel.getIsPrivate())
                .createdAt(channel.getCreatedAt())
                .build();
    }
}