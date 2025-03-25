package com.sabinghost19.teamslkghostapp.dto.registerRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {
    private UUID id;
    private UUID teamId;
    private UUID channelId;
    private UUID uploadedById;
    private String fileName;
    private String fileType;
    private Integer fileSize;
    private String awsS3Key;
    private String url;
    private Instant uploadedAt;
}