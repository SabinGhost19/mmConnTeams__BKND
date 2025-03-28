package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private UUID id;
    private UUID channelId;
    private UUID senderId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileDTO> attachments;
    private List<ReactionDTO> reactions;
    private Boolean isRead;
}