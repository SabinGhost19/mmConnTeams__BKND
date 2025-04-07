package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ChannelDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamDTO;
import com.sabinghost19.teamslkghostapp.model.Channel;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.repository.ChannelRepository;
import com.sabinghost19.teamslkghostapp.repository.TeamRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import com.sabinghost19.teamslkghostapp.repository.FileRepository;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public List<ChannelDTO> getChannelsForTeam(UUID teamId) {

        return channelRepository.findByTeam_Id(teamId).stream()
                .map(channel -> ChannelDTO.builder()
                        .id(channel.getId())
                        .name(channel.getName())
                        .description(channel.getDescription())
                        .teamId(teamId)
                        .build())
                .collect(Collectors.toList());
    }

    public ChannelDTO getChannelById(UUID channelId) {

        var channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Canal negăsit"));

        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .teamId(channel.getTeam().getId())
                .isPrivate(channel.getIsPrivate())
                .build();
    }
    @Transactional
    public ChannelDTO createChannel(UUID creatorID,ChannelDTO channelDTO) {
        UUID existingTeamId = channelDTO.getTeamId();
        Optional<Team> existingTeam = this.teamRepository.findById(existingTeamId);

        if (existingTeam.isEmpty()) {
            throw new RuntimeException("Team not found with id: " + existingTeamId);
        }

        Channel new_channel = channelDTO.toEntity(creatorID,channelDTO, existingTeam.get());
        Channel savedChannel = channelRepository.save(new_channel);
        return channelDTO.toDto(savedChannel);
    }

    public List<UserDto> getChannelMembers(UUID channelId) {

        return userRepository.findUsersByChannelId(channelId).stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .roles(user.getRoles())
                        .build())
                .collect(Collectors.toList());
    }

    public List<FileDTO> getChannelFiles(UUID channelId) {

        return fileRepository.findByChannelId(channelId).stream()
                .map(file -> FileDTO.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .fileSize(file.getFileSize())
                        .awsS3Key(file.getAwsS3Key())
                        .uploadedAt(file.getUploadedAt())
                        .uploadedById(file.getUploadedBy().getId())
                        .channelId(channelId)
                        .build())
                .collect(Collectors.toList());
    }

    public Integer getChannelCountForTeam(UUID teamId) {
        return this.channelRepository.countByTeamId(teamId);
    }
}