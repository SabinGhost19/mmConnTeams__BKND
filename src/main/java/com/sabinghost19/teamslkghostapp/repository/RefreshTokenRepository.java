package com.sabinghost19.teamslkghostapp.repository;
import com.sabinghost19.teamslkghostapp.model.RefreshToken;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}