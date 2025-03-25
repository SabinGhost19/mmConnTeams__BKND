package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.model.TeamMember;
import com.sabinghost19.teamslkghostapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDTO {
    private UUID id;
    private UUID teamId;
    private UUID userId;
    private String role;
    private Instant joinedAt;


    public TeamMemberDTO toDto(TeamMember teamMember) {
        if (teamMember == null) {
            return null;
        }

        return TeamMemberDTO.builder()
                .id(teamMember.getId())
                .teamId(teamMember.getTeam().getId())
                .userId(teamMember.getUser().getId())
                .role(teamMember.getRole())
                .joinedAt(teamMember.getJoinedAt())
                .build();
    }

    public TeamMember toEntity(TeamMemberDTO teamMemberDTO, Team team, User user) {
        if (teamMemberDTO == null) {
            return null;
        }

        return TeamMember.builder()
                .id(teamMemberDTO.getId())
                .team(team)
                .user(user)
                .role(teamMemberDTO.getRole())
                .joinedAt(teamMemberDTO.getJoinedAt())
                .build();
    }
}