package com.sabinghost19.teamslkghostapp.controller;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.request.LoginUserRequest;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.request.RefreshTokenRequest;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.request.RegisterUserRequest;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.response.LoginUserReponseDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.response.RefreshTokenResponseDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.response.RegisterUserResponseDTO;
import com.sabinghost19.teamslkghostapp.security.jwt.JwtTokenProvider;
import com.sabinghost19.teamslkghostapp.services.LoginService;
import com.sabinghost19.teamslkghostapp.services.RegisterService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterService registerService;
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    public AuthController(RegisterService registerService, LoginService loginService,JwtTokenProvider jwtTokenProvider) {
         this.registerService=registerService;
         this.loginService=loginService;
         this.jwtTokenProvider=jwtTokenProvider;
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valid", false);
            response.put("message", "Authorization header missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtTokenProvider.validateToken(token);

        response.put("valid", isValid);
        response.put("message", isValid ? "Token is valid" : "Token is invalid");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserReponseDTO>loginUser(@Valid @RequestBody LoginUserRequest request) {
        LoginUserReponseDTO loginUserReponseDTO = this.loginService.login(request);

        if (loginUserReponseDTO.isSuccess()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(loginUserReponseDTO);
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginUserReponseDTO);
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO>refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponseDTO refreshTokenResponseDTO = this.loginService.refreshToken(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(refreshTokenResponseDTO);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> logout(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        loginService.logout(user.getId());

        //set user offline
        this.loginService.UserOffline(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> test() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Current authentication: " + auth);
        System.out.println("Is authenticated: " + (auth != null && auth.isAuthenticated()));
        return ResponseEntity.ok("JWT test: " + (auth != null ? auth.getName() : "no auth"));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> registerUser(@RequestPart("userData") RegisterUserRequest request,
                                                                @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        logger.info("request venit in registerUser");
        logger.info("Requestul este: " + request);

        String pw_hash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        request.setPassword(pw_hash);
        request.setConfirmPassword(pw_hash);

        User savedUser=this.registerService.registerUser(request);


       if(profileImage!=null && !profileImage.isEmpty()){
            //save the profile image in cloud or somewhere
          if (!this.registerService.uploadProfileImage(profileImage)) {
              //daca a crapat...handle
          }
        }

       RegisterUserResponseDTO registerUserResponseDTO=
                RegisterUserResponseDTO.builder().
                        message("registrated succesfully").
                        success(true).
                        user(new UserDto(savedUser)).
                        build();

        return ResponseEntity.status(HttpStatus.CREATED).body(registerUserResponseDTO);
    }

}
