package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.RegisterUserRequest;
import com.sabinghost19.teamslkghostapp.exceptions.EmailAlreadyExistsException;
import com.sabinghost19.teamslkghostapp.exceptions.PasswordMismatchException;
import com.sabinghost19.teamslkghostapp.repository.UserProfileRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
//@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final Logger logger = Logger.getLogger(AuthController.class.getName());
    private UserProfileRepository userProfileRepository;
    private UserRepository userRepository;

    @Autowired
    public AuthController(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterUserRequest request) {
        logger.info("request venit in registerUser");
        logger.info("Requestul este: " + request);

        if (!request.getPassword().equals(request.getConfirmPassword())){
            //throw error
            throw new PasswordMismatchException("Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            //throw error
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User new_user=User.builder().
                firstName(request.getFirstName()).
                lastName(request.getLastName()).
                email(request.getEmail()).
                password(request.getPassword()).
                role(request.getRole()).
                build();

        userRepository.save(new_user);

        return "saf";
    }

}
