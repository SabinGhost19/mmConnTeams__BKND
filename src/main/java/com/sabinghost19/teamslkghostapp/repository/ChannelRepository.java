package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,UUID> {

    List<Channel> findByTeam_Id(UUID teamId);

    Collection<Object> findByTeamId(UUID teamId);

    @Query("SELECT COUNT(c) FROM Channel c WHERE c.team.id = :teamId")
    Integer countByTeamId(@Param("teamId") UUID teamId);
}
