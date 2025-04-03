package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;
import com.sabinghost19.teamslkghostapp.model.Team;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDetailsDTO {
    private UUID id;
    private String name;
    private String iconUrl;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer messageNr;
    private Integer reactionNr;
    private Integer channelNr;


    public TeamDetailsDTO toDto(Team team,Integer messageNr,Integer reactionNr,Integer channelNr) {
        if (team == null) {
            return null;
        }

        return TeamDetailsDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .iconUrl(team.getIconUrl())
                .description(team.getDescription())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .messageNr(messageNr)
                .reactionNr(reactionNr)
                .channelNr(channelNr)
                .build();
    }


}

