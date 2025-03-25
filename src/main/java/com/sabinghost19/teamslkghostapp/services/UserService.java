package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService{

    private final UserRepository userRepository;

    @Transactional
    public void updateStatus(UUID userId, String status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            user.setUpdatedAt(Instant.from(LocalDateTime.now()));
            userRepository.save(user);
        });
    }

    public List<User>getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        return  userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }
}