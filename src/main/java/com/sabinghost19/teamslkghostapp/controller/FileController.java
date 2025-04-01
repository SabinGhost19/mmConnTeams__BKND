package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    private final FileService fileService;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("team_id") UUID teamId,
            @RequestParam("channel_id") UUID channelId,
            @RequestAttribute("userId") UUID  uploadedBy) {

        //logging
        logger.info(String.valueOf(uploadedBy));
        logger.info(file.getOriginalFilename());

        /// /!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //de revizuit conflicte cu AttachementDTO
        //nu stiu daca trebuie attachement chiar??
        //de vazut
        //DA , nu cred ca e ca ce are FILE In db are si Attachement....
        FileDTO uploadedFile = fileService.uploadFile(file, teamId,channelId, uploadedBy);
        return ResponseEntity.ok(uploadedFile);
    }
}