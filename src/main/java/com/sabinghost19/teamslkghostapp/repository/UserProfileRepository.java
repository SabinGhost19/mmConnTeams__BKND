package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId(UUID userId);

    Optional<UserProfile> findByUser(User user);

    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.user.email = :email")
    Optional<UserProfile> findByUserEmail(@Param("email") String email);
}
