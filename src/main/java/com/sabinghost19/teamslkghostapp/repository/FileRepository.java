package com.sabinghost19.teamslkghostapp.repository;
import com.sabinghost19.teamslkghostapp.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface FileRepository extends JpaRepository<File,UUID> {
    List<File> findByTeamId(UUID teamId);
    List<File> findByChannelId(UUID channelId);
    List<File> findByUploadedById(UUID uploadedById);
    List<File> findByMessageId(UUID messageId);
}
