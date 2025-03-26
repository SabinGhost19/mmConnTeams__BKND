package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
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