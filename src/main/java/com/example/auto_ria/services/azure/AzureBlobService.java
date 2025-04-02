package com.example.auto_ria.services.azure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobStorageException;
import com.example.auto_ria.exceptions.file.FileTransferException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AzureBlobService {

    private BlobServiceClient blobServiceClient;

    private String containerName = "car-api-image-container";

    public AzureBlobService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    public ResponseEntity<byte[]> downloadImage(String filename) {
        log.info("Downloading image: {}", filename);

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(filename);

        if (!blobClient.exists()) {
            log.warn("Image not found: {}", filename);
            throw new FileTransferException("Image not found: " + filename);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobClient.downloadStream(outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            BlobProperties properties = blobClient.getProperties();
            String contentType = properties.getContentType() != null ? properties.getContentType()
                    : "application/octet-stream";

            log.info("Successfully downloaded image: {}", filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(imageBytes.length)
                    .body(imageBytes);

        } catch (BlobStorageException e) {
            log.error("Azure Blob Storage error while downloading image: {}", filename, e);
            throw new FileTransferException("Error downloading image: " + filename);

        } catch (IOException e) {
            log.error("IO error while downloading image: {}", filename, e);
            throw new FileTransferException("Internal error occurred while processing image: " + filename);
        } catch (Exception e) {
            log.error("Unexpected error while downloading image: {}", filename, e);
            throw new FileTransferException("Unexpected error occurred: " + filename);
        }
    }

    public List<String> uploadMultipleImages(MultipartFile[] files) {
        List<String> uploadedFileNames = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String uniqueFileName = uploadSingleFile(file);
                uploadedFileNames.add(uniqueFileName);
            }
        } catch (FileTransferException e) {
            deleteFiles(uploadedFileNames);
            throw new FileTransferException("Batch upload failed. Rolled back.");
        }

        return uploadedFileNames;
    }

    public String uploadSingleFile(MultipartFile file) {
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);

        try {
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));

            return uniqueFileName;
        } catch (IOException | BlobStorageException e) {
            throw new FileTransferException("File upload failed: " + uniqueFileName);
        }
    }

    public void deleteFiles(List<String> fileNames) {
        for (String fileName : fileNames) {
            deleteFile(fileName);
        }
    }

    public void deleteFile(String fileName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        try {
            blobClient.delete();
            log.info("Successfully deleted file: {}", fileName);
        } catch (BlobStorageException e) {
            throw new FileTransferException("File delete failed: " + fileName);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String sanitizedFileName = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("\\.{2,}", ".")
                .replaceAll("^\\.+", "");
        return UUID.randomUUID() + "-" + sanitizedFileName;
    }

}
