package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.model.TeamMember;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.TeamRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamDTO;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamDTO createTeam(TeamDTO teamDTO, User creator) {
        // Creare nouă echipă
        Team team = Team.builder()
                .name(teamDTO.getName())
                .description(teamDTO.getDescription())
                .iconUrl(teamDTO.getIconUrl()) // Opțional
                .build();

        // Salvăm echipa
        Team savedTeam = teamRepository.save(team);
        System.out.println("CREATOR IS: " + creator);

        // Creăm un membru pentru echipă (creatorul)
        TeamMember teamMember = TeamMember.builder()
                .team(savedTeam)
                .user(creator)
                .role("admin") // Creatorul este admin
                .build();

        // Adăugăm membrul la echipă
        savedTeam.getMembers().add(teamMember);
        teamRepository.save(savedTeam);

        // Returnăm DTO
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
}