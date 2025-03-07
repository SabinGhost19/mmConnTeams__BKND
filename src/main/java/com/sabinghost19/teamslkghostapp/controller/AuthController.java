package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.RegisterUserRequest;
import com.sabinghost19.teamslkghostapp.repository.UserProfileRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserProfileRepository userProfileRepository;
    private UserRepository userRepository;

    @Autowired
    public AuthController(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterUserRequest request) {

        User new_user=User.builder().email(request.getEmail()).password(request.getPassword()).build();
        userRepository.save(new_user);
        return "saf";
    }

}
