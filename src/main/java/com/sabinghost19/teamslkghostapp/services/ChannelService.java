package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.ChannelDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.repository.ChannelRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import com.sabinghost19.teamslkghostapp.repository.FileRepository;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

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
}