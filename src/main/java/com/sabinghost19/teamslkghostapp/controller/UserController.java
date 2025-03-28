package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


//    @GetMapping("/current")
//    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            // Presupunând că UserService are o metodă pentru a obține utilizatorul după email/username
//            return ResponseEntity.ok(userService.getUserByEmail(userDetails.getUsername()));
//        }
//        return ResponseEntity.status(401).body("User not authenticated");
//    }
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.isAuthenticated()) {
            UUID userId = (UUID) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("User ID not found");
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(userService.getUserByEmail(userDetails.getUsername()));
        }
        return ResponseEntity.status(401).body("User not authenticated");
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        // Try to get userId from the authentication principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(
            @RequestAttribute("userId") UUID userId,
            @RequestBody Map<String, String> payload) {

        String status = payload.get("status");
        userService.updateStatus(userId, status);
        return ResponseEntity.ok().build();
    }
}