package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.AttachmentDTO;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.repository.FileRepository;
import com.sabinghost19.teamslkghostapp.repository.TeamRepository;
import com.sabinghost19.teamslkghostapp.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("team_id") UUID teamId,
            @RequestParam("channel_id") UUID channelId,
            @RequestParam("uploaded_by") UUID uploadedBy) {

        /// /!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //de revizuit conflicte cu AttachementDTO
        //nu stiu daca trebuie attachement chiar??
        //de vazut
        //DA , nu cred ca e ca ce are FILE In db are si Attachement....
        FileDTO uploadedFile = fileService.uploadFile(file, teamId,channelId, uploadedBy);
        return ResponseEntity.ok(uploadedFile);
    }
}