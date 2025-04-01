package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamUsersMutateDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import com.sabinghost19.teamslkghostapp.enums.Role;
import com.sabinghost19.teamslkghostapp.enums.Status;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.model.UserProfile;
import com.sabinghost19.teamslkghostapp.repository.UserProfileRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService{

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void updateStatus(String userIdStr, String status) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }
    }
    public TeamUsersMutateDTO getCurrentUserDto(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ExecutionException("User profile not found for user: " + user.getId()));

        return mapToTeamUsersMutateDto(user, userProfile);
    }

    private TeamUsersMutateDTO mapToTeamUsersMutateDto(User user, UserProfile userProfile) {
        return TeamUsersMutateDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .status(user.getStatus())
                .profileImage(userProfile.getProfileImageUrl())
                .department(userProfile.getSpecialization())
                .roles(user.getRoles())
                .build();
    }

    @Transactional
    public void updateStatus(UUID userId, String status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            user.setUpdatedAt(Instant.from(LocalDateTime.now()));
            userRepository.save(user);
        });
    }

    public List<UserDto> getAllUserDTOs() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDto convertToDTO(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        // Setează doar proprietățile necesare, evitând relațiile ciclice
        return dto;
    }

    public Optional<User> getUserByEmail(String email) {
        return  userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }
}