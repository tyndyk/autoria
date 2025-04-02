package com.example.auto_ria.controllers.files;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auto_ria.services.azure.AzureBlobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name = "Image Management", description = "Endpoints for managing image uploads and downloads")
public class ImageController {

    private final AzureBlobService azureBlobService;

    @Operation(summary = "Download an image", description = "Retrieves an image from Storage using the provided filename.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved image", content = @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/display/{filename}")
    public ResponseEntity<byte[]> downloadImage(
            @PathVariable @Parameter(description = "The name of the image file to download") String filename) {
        return azureBlobService.downloadImage(filename);
    }
}
