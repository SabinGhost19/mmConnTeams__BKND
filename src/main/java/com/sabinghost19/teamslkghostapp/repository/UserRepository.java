package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User>findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN ChannelMember cm ON u.id = cm.user.id " +
            "WHERE cm.channel.id = :channelId")
    List<User> findUsersByChannelId(@Param("channelId") UUID channelId);

    @Query("SELECT DISTINCT tm.user FROM TeamMember tm WHERE tm.team.id = :teamId")
    List<User> findUsersByTeamId(@Param("teamId") UUID teamId);
}

