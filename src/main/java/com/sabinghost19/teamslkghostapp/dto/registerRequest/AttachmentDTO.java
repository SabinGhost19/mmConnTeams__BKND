package com.sabinghost19.teamslkghostapp.dto.registerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Integer id;
    private Integer messageId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String url;
}