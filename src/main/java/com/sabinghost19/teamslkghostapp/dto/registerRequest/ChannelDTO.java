package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import com.sabinghost19.teamslkghostapp.model.Channel;
import com.sabinghost19.teamslkghostapp.model.Team;
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
public class ChannelDTO {
    private UUID id;
    private UUID teamId;
    private String name;
    private String description;
    private Boolean isPrivate;
    private UUID creatorID;
    private Instant createdAt;
    private Instant updatedAt;

    public ChannelDTO toDto(Channel channel) {
        if (channel == null) {
            return null;
        }

        return ChannelDTO.builder()
                .id(channel.getId())
                .teamId(channel.getTeam().getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .isPrivate(channel.getIsPrivate())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .creatorID(channel.getCreatorID())
                .build();
    }

    public Channel toEntity(UUID creatorId,ChannelDTO channelDTO, Team team) {
        if (channelDTO == null) {
            return null;
        }

        return Channel.builder()
                .id(channelDTO.getId())
                .team(team)
                .name(channelDTO.getName())
                .description(channelDTO.getDescription())
                .isPrivate(channelDTO.getIsPrivate())
                .createdAt(channelDTO.getCreatedAt())
                .updatedAt(channelDTO.getUpdatedAt())
                .creatorID(creatorId)
                .build();
    }
}