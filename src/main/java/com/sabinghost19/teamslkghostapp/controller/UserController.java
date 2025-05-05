package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.enums.Role;
import com.sabinghost19.teamslkghostapp.enums.Status;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.model.UserProfile;
import com.sabinghost19.teamslkghostapp.services.FileService;
import com.sabinghost19.teamslkghostapp.services.TeamService;
import com.sabinghost19.teamslkghostapp.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final TeamService teamService;
    private final FileService fileService;

    @Autowired
    public UserController(UserService userService, TeamService teamService,FileService fileService) {
        this.userService = userService;
        this.teamService = teamService;
        this.fileService = fileService;
    }
    @GetMapping("/current/{memberId}")
    public ResponseEntity<?> getCurrentUser(@PathVariable UUID memberId,Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            TeamUsersMutateDTO userDto = userService.getCurrentUserDto(userDetails.getUsername());

            return ResponseEntity.ok(userDto);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User profile not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user data"));
        }
    }
    @GetMapping("/current/{memberId}/profile")
    public ResponseEntity<UserProfileDTO> getCurrentProfile(@PathVariable("memberId") UUID memberId) {

        UserProfileDTO userProfileDTO=this.userService.getUserProfile(memberId);
        return ResponseEntity.ok(userProfileDTO);
    }

    @GetMapping("/current/profile")
    public ResponseEntity<UserProfileDTO> getCurrentProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;
        UserProfileDTO userProfileDTO=this.userService.getUserProfile(user.getId());
        return ResponseEntity.ok(userProfileDTO);
    }

    @PutMapping("/current/update")
    public ResponseEntity<String>updateUserProfile(@RequestBody UserProfileUpdateDTO userProfileDTO, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;
        this.userService.updateProfile(user.getId(),userProfileDTO);
        return  ResponseEntity.ok("success");
    }

    @PutMapping("/current/update/profileimage")
    public ResponseEntity<String>updateUserProfile(@RequestParam("current_blob_url") String current_blob_url,@RequestParam("new_file") MultipartFile new_file, Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;

        String new_blob_url=this.fileService.updateFile(current_blob_url,new_file);
        this.userService.updateProfileImage(new_blob_url,user.getId());
        return  ResponseEntity.ok("success");
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            TeamUsersMutateDTO userDto = userService.getCurrentUserDto(userDetails.getUsername());

            return ResponseEntity.ok(userDto);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User profile not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user data"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;

        return ResponseEntity.ok(userService.getAllUserDTOs());
    }


    @GetMapping("/common-team/{teamId}")
    public ResponseEntity<?> getAllUsers(@PathVariable UUID teamId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;

        List<User> users = userService.getCommonUsersByTeamID(teamId);

        // Create a detailed list of user data without circular references
        List<Map<String, Object>> detailedUsers = users.stream().map(u -> {
            Map<String, Object> userData = new HashMap<>();
            // User basic info
            userData.put("id", u.getId());
            userData.put("firstName", u.getFirstName());
            userData.put("lastName", u.getLastName());
            userData.put("email", u.getEmail());
            userData.put("status", u.getStatus());
            userData.put("createdAt", u.getCreatedAt());
            userData.put("updatedAt", u.getUpdatedAt());

            // Include roles without the circular reference
            if (u.getRoles() != null) {
                userData.put("roles", u.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toList()));
            }

            // Add profile data if available
            if (u.getProfile() != null) {
                UserProfile profile = u.getProfile();
                Map<String, Object> profileData = new HashMap<>();
                profileData.put("id", profile.getId());
                profileData.put("institution", profile.getInstitution());
                profileData.put("studyLevel", profile.getStudyLevel());
                profileData.put("specialization", profile.getSpecialization());
                profileData.put("year", profile.getYear());
                profileData.put("group", profile.getGroup());
                profileData.put("bio", profile.getBio());
                profileData.put("profileImageUrl", profile.getProfileImageUrl());
                profileData.put("phoneNumber", profile.getPhoneNumber());
                profileData.put("termsAccepted", profile.isTermsAccepted());
                profileData.put("privacyPolicyAccepted", profile.isPrivacyPolicyAccepted());
                profileData.put("createdAt", profile.getCreatedAt());
                profileData.put("updatedAt", profile.getUpdatedAt());

                // Add the profile data to the user object
                userData.put("profile", profileData);
            }

            return userData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(detailedUsers);
    }



    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(
            @RequestAttribute("userId") UUID userId,
            @RequestBody Map<String, String> payload) {

        try {
            String statusStr = payload.get("status");
            userService.updateStatus(userId, statusStr);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value. Valid values are: " +
                    Arrays.toString(Status.values()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating status");
        }
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<?> getAllUsersByTeam(
            @PathVariable UUID teamId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        try {
            List<TeamUsersMutateDTO> userDTOs = teamService.getAllUsersByTeamId(teamId);
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching team users: " + e.getMessage());
        }

    }
}