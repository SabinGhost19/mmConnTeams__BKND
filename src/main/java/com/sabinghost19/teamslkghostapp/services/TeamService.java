package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamUsersMutateDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.model.TeamMember;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.model.UserProfile;
import com.sabinghost19.teamslkghostapp.repository.TeamMemberRepository;
import com.sabinghost19.teamslkghostapp.repository.TeamRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamDTO;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public TeamService( TeamRepository teamRepository, UserRepository userRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
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
                        .description(team.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public TeamDTO getTeamById(UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Echipă negăsită"));

        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }

    public List<UserDto> getTeamMembers(UUID teamId) {
        return userRepository.findUsersByTeamId(teamId).stream()
                .map(UserDto::new)
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
}