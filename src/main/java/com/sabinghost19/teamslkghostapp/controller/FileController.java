package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.services.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    private final FileService blobStorageService;
    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileController(FileService blobStorageService) {
        this.blobStorageService = blobStorageService;
    }

//    @PostMapping("/upload")
//    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
//        String url=blobStorageService.uploadFile(multipartFile);
//        return ResponseEntity.status(HttpStatus.CREATED).body(url);
//    }

    @PostMapping("/upload/explicit")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("team_id") UUID teamId,
            @RequestParam("channel_id") UUID channelId,
            Authentication authentication) throws IOException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;
        //logging
        logger.info(String.valueOf(user.getEmail()));
        logger.info(multipartFile.getOriginalFilename());

        String url = blobStorageService.uploadFile(multipartFile,teamId,channelId,user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @DeleteMapping("/delete/{fileName}")
    public Object handleFileUpload(@PathVariable String fileName) throws IOException {
        blobStorageService.delete(fileName);
        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/download/{fileName}")
    public @ResponseBody byte[] handleFileDownload(HttpServletResponse response, @PathVariable String fileName) throws IOException {
        response.addHeader("Content-Disposition", "attachment; filename="+fileName);
        return blobStorageService.download(fileName);
    }

    @GetMapping("/all/{teamId}")
    public ResponseEntity<List<FileDTO>> getFilesByTeamId(@PathVariable UUID teamId) {
          logger.info("INTRAT IN FILE GET ALL");
           return ResponseEntity.ok().body(this.blobStorageService.getMetadataByTeamId(teamId));
    }

}