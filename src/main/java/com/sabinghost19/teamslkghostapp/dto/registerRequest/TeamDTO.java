package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.sabinghost19.teamslkghostapp.model.Team;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {
    private UUID id;
    private String name;
    private String iconUrl;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public TeamDTO toDto(Team team) {
        if (team == null) {
            return null;
        }

        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .iconUrl(team.getIconUrl())
                .description(team.getDescription())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    public Team toEntity(TeamDTO teamDTO) {
        if (teamDTO == null) {
            return null;
        }

        return Team.builder()
                .id(teamDTO.getId())
                .name(teamDTO.getName())
                .iconUrl(teamDTO.getIconUrl())
                .description(teamDTO.getDescription())
                .createdAt(teamDTO.getCreatedAt())
                .updatedAt(teamDTO.getUpdatedAt())
                .build();
    }
}

