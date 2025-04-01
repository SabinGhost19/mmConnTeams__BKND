package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamMemberDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.TeamUsersMutateDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import com.sabinghost19.teamslkghostapp.enums.Status;
import com.sabinghost19.teamslkghostapp.model.User;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final TeamService teamService;

    @Autowired
    public UserController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
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