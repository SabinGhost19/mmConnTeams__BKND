package com.sabinghost19.teamslkghostapp.repository;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    @Query("SELECT DISTINCT tm.team FROM TeamMember tm WHERE tm.user.id = :userId")
    List<Team> findTeamsByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Team t WHERE t.name = :teamName")
    Optional<Team> findByName(String teamName);


}
