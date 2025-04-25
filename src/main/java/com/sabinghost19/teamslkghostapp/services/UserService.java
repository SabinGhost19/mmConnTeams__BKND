package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamUsersMutateDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserProfileDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserProfileUpdateDTO;
import com.sabinghost19.teamslkghostapp.enums.Role;
import com.sabinghost19.teamslkghostapp.enums.Status;
import com.sabinghost19.teamslkghostapp.model.Team;
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
import java.util.Collections;
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

    public UserProfileDTO getUserProfile(UUID id) {
        UserProfile profile=this.userProfileRepository.findByUserId(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return mapUserProfileToDTO(profile);
    }

    @Transactional
    public void updateProfileImage(String blob_url,UUID user_id) {

        UserProfile userProfile= this.userProfileRepository.findByUserId(user_id)
                .orElseThrow(() -> new UsernameNotFoundException("UserProfile not found for user with id: " + user_id));

        userProfile.setProfileImageUrl(blob_url);
        this.userProfileRepository.save(userProfile);
    }

    @Transactional
    public void updateProfile(UUID user_id,UserProfileUpdateDTO userProfileUpdateDTO) {

        UserProfile userProfile= this.userProfileRepository.findByUserId(user_id)
                 .orElseThrow(() -> new UsernameNotFoundException("UserProfile not found for user with id: " + user_id));
        User user=this.userRepository.findById(user_id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + user_id));

        userProfile.setPhoneNumber(userProfileUpdateDTO.getPhoneNumber());
        userProfile.setYear(userProfileUpdateDTO.getYear());
        userProfile.setGroup(userProfileUpdateDTO.getGroup());
        userProfile.setBio(userProfileUpdateDTO.getBio());

        user.setFirstName(userProfileUpdateDTO.getFirstName());
        user.setLastName(userProfileUpdateDTO.getLastName());
        this.userProfileRepository.save(userProfile);
        this.userRepository.save(user);
    }

    @Transactional
    public void updateStatus(String userIdStr, String status) {
        try {
            UUID userId = UUID.fromString(userIdStr);
            updateStatus(userId, status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }
    }

    public List<TeamUsersMutateDTO>getAllTeamsUsers(){
        return userRepository.findAll().stream()
                .map(this::getCurrentUserDto)
                .collect(Collectors.toList());
    }

    public TeamUsersMutateDTO getCurrentUserDto(User user) {

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);

                    newProfile.setInstitution("Not specified"); // Câmpul institution este obligatoriu
                    return userProfileRepository.save(newProfile);
                });

        return mapToTeamUsersMutateDto(user, userProfile);
    }

    public TeamUsersMutateDTO getCurrentUserDto(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);

                    newProfile.setInstitution("Not specified"); // Câmpul institution este obligatoriu
                    return userProfileRepository.save(newProfile);
                });

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

    public static UserProfileDTO mapUserProfileToDTO(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }

        return UserProfileDTO.builder()
                .id(userProfile.getId())
                .userId(userProfile.getUser() != null ? userProfile.getUser().getId() : null)
                .institution(userProfile.getInstitution())
                .studyLevel(userProfile.getStudyLevel())
                .specialization(userProfile.getSpecialization())
                .year(userProfile.getYear())
                .group(userProfile.getGroup())
                .bio(userProfile.getBio())
                .profileImageUrl(userProfile.getProfileImageUrl())
                .phoneNumber(userProfile.getPhoneNumber())
                .termsAccepted(userProfile.isTermsAccepted())
                .privacyPolicyAccepted(userProfile.isPrivacyPolicyAccepted())
                .createdAt(userProfile.getCreatedAt())
                .updatedAt(userProfile.getUpdatedAt())
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

    public List<User> getCommonUsersByTeamID(UUID teamId) {
        return this.userRepository.findUsersByTeamId(teamId);
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