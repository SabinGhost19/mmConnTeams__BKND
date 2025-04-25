package com.sabinghost19.teamslkghostapp.services;



import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.FileDTO;
import com.sabinghost19.teamslkghostapp.model.File;
import com.sabinghost19.teamslkghostapp.model.Team;
import com.sabinghost19.teamslkghostapp.model.Channel;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.FileRepository;
import com.sabinghost19.teamslkghostapp.repository.TeamRepository;
import com.sabinghost19.teamslkghostapp.repository.ChannelRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.ParallelTransferOptions;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final int DEFAULT_BLOCK_SIZE = 4 * 1024 * 1024; // 4 MB
    private static final int DEFAULT_NUM_BUFFERS = 8;
    private final FileRepository fileRepository;
    private final TeamRepository teamRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BlobContainerAsyncClient blobContainerAsyncClient;
    private final String blobBaseUrl;



    @Autowired
    public FileService(BlobContainerAsyncClient blobContainerAsyncClient,FileRepository fileRepository, TeamRepository teamRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.teamRepository = teamRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.blobContainerAsyncClient = blobContainerAsyncClient;
        this.blobBaseUrl = blobContainerAsyncClient.getBlobContainerUrl();
    }

    public Integer getNumberOfFiles(){
        return this.fileRepository.findAll().size();
    }

    public List<FileDTO> getMetadataByTeamId(UUID teamId) {
        // verify the team exists
        teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Channel not found with id: " + teamId));

        List<File> files = fileRepository.findByTeamId(teamId);

        return files.stream()
                .map(file -> {
                    FileDTO dto = new FileDTO();
                    dto.setId(file.getId());
                    dto.setTeamId(file.getTeam() != null ? file.getTeam().getId() : null);
                    dto.setChannelId(file.getChannel() != null ? file.getChannel().getId() : null);
                    dto.setUploadedById(file.getUploadedBy() != null ? file.getUploadedBy().getId() : null);
                    dto.setFileName(file.getFileName());
                    dto.setFileType(file.getFileType());
                    dto.setFileSize(file.getFileSize());
                    dto.setAwsS3Key(file.getAwsS3Key());
                    dto.setUrl(file.getUrl());
                    dto.setUploadedAt(file.getUploadedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public String updateFile(String currentBlobUrl, MultipartFile newFile) throws IOException {
        if (newFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // extract file key from URL (file name in blob storage)
        String currentBlobKey = null;
        if (currentBlobUrl != null) {
            // extract file name from blob URL
            // assuming URL format: https://caribytestblob19.blob.core.windows.net/caribu-container/[FILENAME]
            String[] parts = currentBlobUrl.split("/");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                // remove SAS parameters if they exist
                if (lastPart.contains("?")) {
                    currentBlobKey = lastPart.substring(0, lastPart.indexOf("?"));
                } else {
                    currentBlobKey = lastPart;
                }
            }
        }

        // delete old file from blob storage if it exists
        if (currentBlobKey != null) {
            try {
                BlobAsyncClient oldBlobClient = blobContainerAsyncClient.getBlobAsyncClient(currentBlobKey);
                oldBlobClient.delete().block();
                logger.info("Old file deleted: {}", currentBlobKey);
            } catch (Exception e) {
                logger.warn("Could not delete old file: {}", e.getMessage());
                // continue the process even if deletion failed
            }
        }

        // upload new file
        String newFileNameUnique = generateUniqueFileName(newFile.getOriginalFilename());
        String fileName = newFile.getOriginalFilename();
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(newFileNameUnique);

        byte[] fileContent = newFile.getBytes();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(fileContent));

        ParallelTransferOptions transferOptions = new ParallelTransferOptions()
                .setBlockSizeLong((long) DEFAULT_BLOCK_SIZE)
                .setMaxConcurrency(DEFAULT_NUM_BUFFERS);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(newFile.getContentType());

        try {
            blobAsyncClient.upload(data, transferOptions, true)
                    .then(blobAsyncClient.setHttpHeaders(headers))
                    .block();

            logger.info("New file successfully uploaded: {}", newFileNameUnique);

            // generate SAS token for access
            OffsetDateTime expiryTime = OffsetDateTime.now().plusYears(1);
            BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime,
                    new BlobSasPermission().setReadPermission(true));
            String sasToken = blobAsyncClient.generateSas(sasValues);
            String newBlobUrlWithSas = blobAsyncClient.getBlobUrl() + "?" + sasToken;

            return newBlobUrlWithSas;
        } catch (Exception e) {
            logger.error("Error updating file: {}", e.getMessage());
            throw new IOException("Error updating file: " + e.getMessage(), e);
        }
    }


    @Transactional
    public String uploadFile_ProfileImage(MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File EMPTY");
        }

        String fileNameUnique = generateUniqueFileName(file.getOriginalFilename());
        String fileName = file.getOriginalFilename();
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(fileNameUnique);

        byte[] fileContent = file.getBytes();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(fileContent));

        ParallelTransferOptions transferOptions = new ParallelTransferOptions()
                .setBlockSizeLong((long) DEFAULT_BLOCK_SIZE)
                .setMaxConcurrency(DEFAULT_NUM_BUFFERS);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());

        try {
            blobAsyncClient.upload(data, transferOptions, true)
                    .then(blobAsyncClient.setHttpHeaders(headers))
                    .block();

            logger.info("File successfully uploaded: {}", fileNameUnique);

            // generate SAS token with read permission that expires in 1 year (or adjust as needed)
            OffsetDateTime expiryTime = OffsetDateTime.now().plusYears(1);

            BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime,
                    new BlobSasPermission().setReadPermission(true));

            // generate the SAS token and create a URL with the SAS token
            String sasToken = blobAsyncClient.generateSas(sasValues);
            String blobUrlWithSas = blobAsyncClient.getBlobUrl() + "?" + sasToken;

            File fileEntity = File.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .fileSize((int) file.getSize())
                    .awsS3Key(fileNameUnique)
                    .url(blobUrlWithSas) // save the URL with SAS token
                    .uploadedBy(user)
                    .build();

            fileRepository.save(fileEntity);

            return blobUrlWithSas; // return the URL with SAS token
        } catch (Exception e) {
            logger.error("Eroare la încărcarea fișierului: {}", e.getMessage());
            throw new IOException("Eroare la încărcarea fișierului: " + e.getMessage(), e);
        }
    }


    @Transactional
    public String uploadFile(
            MultipartFile file,
            UUID teamId,
            UUID channelId,
            UUID uploadedBy) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File EMPTY");
        }

        String fileNameUnique = generateUniqueFileName(file.getOriginalFilename());
        String fileName=file.getOriginalFilename();
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(fileNameUnique);

        byte[] fileContent = file.getBytes();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(fileContent));

        ParallelTransferOptions transferOptions = new ParallelTransferOptions()
                .setBlockSizeLong((long) DEFAULT_BLOCK_SIZE)
                .setMaxConcurrency(DEFAULT_NUM_BUFFERS);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());

        try {
            blobAsyncClient.upload(data, transferOptions, true)
                    .then(blobAsyncClient.setHttpHeaders(headers))
                    .block();

            logger.info("File successfully uploaded: {}", fileNameUnique);

            String blobUrl = blobAsyncClient.getBlobUrl();

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

            File fileEntity = File.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .fileSize((int) file.getSize())
                    .awsS3Key(fileNameUnique)
                    .url(blobUrl)
                    .team(team)
                    .channel(channel)
                    .uploadedBy(user)
                    .build();

            fileRepository.save(fileEntity);

            return blobUrl;
        } catch (Exception e) {
            logger.error("Eroare la încărcarea fișierului: {}", e.getMessage());
            throw new IOException("Eroare la încărcarea fișierului: " + e.getMessage(), e);
        }
    }

    public String upload(Resource resource) throws IOException {
        String fileName = resource.getFilename();
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(fileName);

        byte[] content = resource.getInputStream().readAllBytes();
        Flux<ByteBuffer> data = Flux.just(ByteBuffer.wrap(content));

        ParallelTransferOptions transferOptions = new ParallelTransferOptions()
                .setBlockSizeLong((long) DEFAULT_BLOCK_SIZE)
                .setMaxConcurrency(DEFAULT_NUM_BUFFERS);

        blobAsyncClient.upload(data, transferOptions, true).block();

        return blobAsyncClient.getBlobUrl();
    }

    public boolean delete(String fileName) {
        try {
            BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(fileName);
            blobAsyncClient.delete().block();
            logger.info("Fișier șters cu succes: {}", fileName);
            return true;
        } catch (Exception e) {
            logger.error("Eroare la ștergerea fișierului {}: {}", fileName, e.getMessage());
            return false;
        }
    }

    public byte[] download(String fileName) throws IOException {
        try {
            BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(fileName);
            return blobAsyncClient.downloadContent().block().toBytes();
        } catch (Exception e) {
            logger.error("Eroare la descărcarea fișierului {}: {}", fileName, e.getMessage());
            throw new IOException("Eroare la descărcarea fișierului: " + e.getMessage(), e);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    public String getBlobUrl(String fileName) {
        return blobContainerAsyncClient.getBlobAsyncClient(fileName).getBlobUrl();
    }
}