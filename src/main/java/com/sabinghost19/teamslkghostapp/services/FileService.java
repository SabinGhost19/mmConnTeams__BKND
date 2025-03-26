package com.sabinghost19.teamslkghostapp.services;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.PutObjectRequest;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.model.Channel;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.FileRepository;
import com.sabinghost19.teamslkghostapp.repository.TeamRepository;
import com.sabinghost19.teamslkghostapp.repository.ChannelRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final TeamRepository teamRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    //private final AmazonS3 amazonS3Client;

    @Autowired
    public FileService(FileRepository fileRepository, TeamRepository teamRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.teamRepository = teamRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }
    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.files-prefix}")
    private String filesPrefix;

    @Transactional
    public FileDTO uploadFile(
            MultipartFile multipartFile,
            UUID teamId,
            UUID channelId,
            UUID uploadedBy) {
//        try {

            if (multipartFile.isEmpty()) {
                throw new IllegalArgumentException("Fișier gol");
            }


            User user = userRepository.findById(uploadedBy)
                    .orElseThrow(() -> new RuntimeException("Utilizator negăsit"));

            Team team = teamId != null
                    ? teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("Echipă negăsită"))
                    : null;

            Channel channel = channelId != null
                    ? channelRepository.findById(channelId)
                    .orElseThrow(() -> new RuntimeException("Canal negăsit"))
                    : null;

            // Generare nume unic pentru fișier în S3
            String originalFileName = multipartFile.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID() + fileExtension;
            String s3Key = filesPrefix + uniqueFileName;


//            PutObjectRequest putObjectRequest = new PutObjectRequest(
//                    bucketName,
//                    s3Key,
//                    multipartFile.getInputStream(),
//                    null
//            );
//            amazonS3Client.putObject(putObjectRequest);

            // Generare URL S3
           // String s3Url = amazonS3Client.getUrl(bucketName, s3Key).toString();

//            File file = File.builder()
//                    .team(team)
//                    .channel(channel)
//                    .uploadedBy(user)
//                    .fileName(originalFileName)
//                    .fileType(multipartFile.getContentType())
//                    .fileSize((int) multipartFile.getSize())
//                    .awsS3Key(s3Key)
//                    .url(s3Url)
//                    .build();

//            file = fileRepository.save(file);
//
//            return FileDTO.builder()
//                    .id(file.getId())
//                    .teamId(team != null ? team.getId() : null)
//                    .channelId(channel != null ? channel.getId() : null)
//                    .uploadedById(user.getId())
//                    .fileName(file.getFileName())
//                    .fileType(file.getFileType())
//                    .fileSize(file.getFileSize())
//                    .awsS3Key(file.getAwsS3Key())
//                    .url(file.getUrl())
//                    .uploadedAt(file.getUploadedAt())
//                    .build();

//        } catch (IOException ex) {
//            log.error("Eroare la încărcarea fișierului", ex);
//            throw new RuntimeException("Nu s-a putut salva fișierul", ex);
//        }

        return new FileDTO();
    }
}