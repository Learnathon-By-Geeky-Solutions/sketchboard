package com.example.lostnfound.controller;

import com.example.lostnfound.model.Image;
import com.example.lostnfound.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@Tag(name = "Image APIs", description = "REST APIs for image operations")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    @Operation(summary = "Upload an image", description = "Upload an image file and return the image details")
    public ResponseEntity<Image> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        Image savedImage = imageService.saveImage(file);
        return ResponseEntity.ok(savedImage);
    }

    @GetMapping("/{imageId}")
    @Operation(summary = "Get an image", description = "Get an image by its ID")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId) throws IOException {
        Resource resource = imageService.loadImage(imageId);
        String contentType = resource.getFile().toPath().getFileName().toString().toLowerCase();
        String mimeType = "image/jpeg"; // default to JPEG
        if (contentType.endsWith(".png")) {
            mimeType = "image/png";
        } else if (contentType.endsWith(".gif")) {
            mimeType = "image/gif";
        } else if (contentType.endsWith(".webp")) {
            mimeType = "image/webp";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete an image", description = "Delete an image by its ID")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok().build();
    }
}