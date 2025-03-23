package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.model.RefreshToken;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.RefreshTokenRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import com.sabinghost19.teamslkghostapp.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.response.RefreshTokenResponseDTO;

import java.time.Instant;
import java.util.UUID;
@Service
@Transactional
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken existingToken = refreshTokenRepository.findByUser(user).orElse(null);
        if (existingToken != null) {
            refreshTokenRepository.delete(existingToken);
            refreshTokenRepository.flush();
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenResponseDTO refreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtTokenProvider.generateTokenFromUser(user);

                    return RefreshTokenResponseDTO.builder()
                            .success(true)
                            .message("Token refreshed successfully")
                            .token(newAccessToken)
                            .refreshToken(token)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired");
        }

        return token;
    }

    public void deleteByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.deleteByUser(user);
    }
}