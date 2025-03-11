package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.NotificationPreferencesDto;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.RegisterUserRequest;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.RegisterUserResponseDTO;
import com.sabinghost19.teamslkghostapp.exceptions.EmailAlreadyExistsException;
import com.sabinghost19.teamslkghostapp.exceptions.PasswordMismatchException;
import com.sabinghost19.teamslkghostapp.model.NotificationPreferences;
import com.sabinghost19.teamslkghostapp.repository.NotificationPreferencesRepository;
import com.sabinghost19.teamslkghostapp.repository.UserProfileRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;

@RestController
//@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;
    @Autowired
    public AuthController(UserProfileRepository userProfileRepository, UserRepository userRepository,NotificationPreferencesRepository notificationPreferencesRepository) {
        this.notificationPreferencesRepository = notificationPreferencesRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> registerUser( @RequestPart("userData") RegisterUserRequest request,
                                                                 @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
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
       if(profileImage!=null && !profileImage.isEmpty()){
            //save the profile image in cloud or somewhere
        }
       //build based on request and save NotPref
       NotificationPreferencesDto prefNDTO=request.getNotificationPreferences();
       NotificationPreferences prefNtoBeSaved=NotificationPreferences.builder().
               user(new_user).email(prefNDTO.isEmail()).push(prefNDTO.isPush()).
               desktop(prefNDTO.isDesktop()).build();
       this.notificationPreferencesRepository.save(prefNtoBeSaved);

       //make response
       RegisterUserResponseDTO registerUserResponseDTO=
                RegisterUserResponseDTO.builder().
                        message("registrated succesfully").
                        success(true).
                        token("TOKEN AICI").
                        user(new RegisterUserResponseDTO.
                                UserDto(new_user)).
                        build();

        return ResponseEntity.status(HttpStatus.CREATED).body(registerUserResponseDTO);
    }

}
