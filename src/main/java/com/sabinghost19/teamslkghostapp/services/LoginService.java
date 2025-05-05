package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.request.LoginUserRequest;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.response.LoginUserReponseDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.UserDto;
import com.sabinghost19.teamslkghostapp.model.RefreshToken;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import com.sabinghost19.teamslkghostapp.security.jwt.JwtTokenProvider;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.response.RefreshTokenResponseDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.request.RefreshTokenRequest;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(LoginService.class.getName());
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public LoginService(UserRepository userRepository,AuthenticationManager authenticationManager,JwtTokenProvider tokenProvider,RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public void UserOnline(User user) {
        user.setStatus("ONLINE");
        this.userRepository.save(user);
    }

    @Transactional
    public void UserOffline(User user) {
        user.setStatus("OFFLINE");
        this.userRepository.save(user);
    }

    public LoginUserReponseDTO login(LoginUserRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String jwt = tokenProvider.generateJwtToken(authentication);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            //must be transactional here
            this.UserOnline(user);

            return LoginUserReponseDTO.builder()
                    .success(true)
                    .message("Login successful")
                    .user(new UserDto(user))
                    .token(jwt)
                    .refreshToken(refreshToken.getToken())
                    .roles(user.getRoles())
                    .build();
        } catch (BadCredentialsException e) {
            return LoginUserReponseDTO.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }
    }


    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.refreshToken(request.getRefreshToken());
    }

    @Transactional
    public void logout(UUID userId) {
        this.userRepository.findById(userId).ifPresent(user -> {user.setStatus("OFFLINE");
        this.userRepository.save(user);
        });
        refreshTokenService.deleteByUserId(userId);
    }

    public LoginUserReponseDTO verifyUser(String email, String candidate_password) {
        Optional<User> userBD=this.userRepository.findByEmail(email);
        LoginUserReponseDTO loginUserReponseDTO;
        if(userBD.isEmpty()){
            //user is not present
            //give error
            logger.info("USER AUTH FAILED....");
            loginUserReponseDTO=LoginUserReponseDTO
                    .builder()
                    .message("Login Failed User does not exist...")
                    .success(false)
                    .build();
            return loginUserReponseDTO;
        }
        if (!BCrypt.checkpw(candidate_password, userBD.get().getPassword())) {
            System.out.println("It does not match");
            loginUserReponseDTO=LoginUserReponseDTO
                    .builder()
                    .message("Login Failed Password not Match...")
                    .success(false)
                    .build();
            logger.info("USER AUTH FAILED....");
            return loginUserReponseDTO;
        }
        UserDto userResp=new UserDto(userBD.get());
        String token="mocking the token here";
        loginUserReponseDTO=LoginUserReponseDTO
                .builder()
                .message("Login Succesfull...")
                .user(userResp)
                .success(true)
                .token(token)
                .build();
        logger.info("USER IS AUTHENTICATED....");
        return loginUserReponseDTO;
    }
}
