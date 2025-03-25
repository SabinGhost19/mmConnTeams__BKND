package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Integer id;
    private Integer channelId;
    private Integer senderId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AttachmentDTO> attachments;
    private List<ReactionDTO> reactions;
    private Boolean isRead;
}