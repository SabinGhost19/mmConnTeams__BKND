package com.sabinghost19.teamslkghostapp.repository;

import com.sabinghost19.teamslkghostapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT DISTINCT m FROM Message m " +
            "LEFT JOIN FETCH m.channel " +
            "LEFT JOIN FETCH m.sender " +
            "LEFT JOIN FETCH m.attachments att " +
            "LEFT JOIN FETCH att.team " +
            "LEFT JOIN FETCH att.channel " +
            "LEFT JOIN FETCH att.uploadedBy " +
            "LEFT JOIN FETCH m.reactions r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.channel " +
            "WHERE m.channel.id = :channelId " +
            "ORDER BY m.createdAt ASC")
    List<Message> findByChannelIdWithAll(@Param("channelId") UUID channelId);


    @Query("SELECT COUNT(m) FROM Message m JOIN m.channel c WHERE c.team.id = :teamId")
    Integer countByChannelTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT m FROM Message m " +
            "LEFT JOIN MessageReadStatus mrs ON m.id = mrs.message.id AND mrs.user.id = :userId " +
            "WHERE m.channel.id = :channelId AND (mrs.isRead = false OR mrs IS NULL)")
    List<Message> findUnreadMessagesForUser(
            @Param("channelId") UUID channelId,
            @Param("userId") UUID userId
    );

    List<Message> findByChannel_IdOrderByCreatedAtAsc(UUID channelId);

    @Query("SELECT DISTINCT m FROM Message m " +
            "LEFT JOIN FETCH m.sender " +
            "LEFT JOIN FETCH m.attachments " +
            "LEFT JOIN FETCH m.reactions " +
            "LEFT JOIN m.readStatuses rs " +
            "WHERE m.channel.id = :channelId " +
            "ORDER BY m.createdAt ASC")
    List<Message> findDetailedMessagesByChannelId(
            @Param("channel_Id") UUID channelId
    );

    @Query("SELECT m FROM Message m " +
            "WHERE m.channel.id = :channelId AND m.createdAt > :timestamp " +
            "ORDER BY m.createdAt ASC")
    List<Message> findMessagesAfterTimestamp(@Param("channel_Id") UUID channelId, @Param("timestamp") Instant timestamp);

}
