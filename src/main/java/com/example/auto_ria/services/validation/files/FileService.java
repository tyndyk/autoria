package com.example.auto_ria.services.validation.files;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.auto_ria.exceptions.file.FileValidationException;
import com.example.auto_ria.models.user.UserSQL;
import com.example.auto_ria.services.azure.AzureBlobService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileService {

    private AzureBlobService azureBlobService;

    private void validateFile(MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            throw new FileValidationException("Picture is required");
        }

        if (!isValidImage(avatar.getContentType())) {
            throw new FileValidationException(
                    avatar.getOriginalFilename() + "Invalid file type. Only image files are allowed");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (avatar.getSize() > maxSize) {
            throw new FileValidationException(avatar.getOriginalFilename() + "is too large. Max size is 5MB");
        }
    }

    private boolean isValidImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    public void deleteExistingAvatar(UserSQL user) {
        if (user.getAvatar() != null) {
            azureBlobService.deleteFile(user.getAvatar());
        }
    }

    public void deleteFiles(List<String> photos) {
        if (photos.isEmpty())
            throw new FileValidationException("No photos to delete");
        azureBlobService.deleteFiles(photos);
    }

    public String uploadFile(MultipartFile avatar) {
        validateFile(avatar);
        return azureBlobService.uploadSingleFile(avatar);
    }

    public List<String> uploadFiles(MultipartFile[] photos) {
        List<String> errors = new ArrayList<>();
        for (MultipartFile photo : photos) {
            try {
                validateFile(photo);
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            throw new FileValidationException(errors.toString());
        }

        return azureBlobService.uploadMultipleImages(photos);
    }
}
