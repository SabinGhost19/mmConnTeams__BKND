package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
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