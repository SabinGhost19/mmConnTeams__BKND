package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.TeamMember;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {

    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    List<TeamMember> findByTeamId(UUID teamId);

    @Query("SELECT tm.user FROM TeamMember tm WHERE tm.team.id = :teamId")
    List<User> findUsersByTeamId(@Param("teamId") UUID teamId);


    @Query("SELECT tm.user FROM TeamMember tm JOIN FETCH tm.user.profile WHERE tm.team.id = :teamId")
    List<User> findUsersWithProfileByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.user WHERE tm.team.id = :teamId")
    List<TeamMember> findTeamMembersWithUsersByTeamId(@Param("teamId") UUID teamId);


}
