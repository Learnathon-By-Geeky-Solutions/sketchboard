package com.example.lostnfound.controller;

import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.model.Image;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@Tag(name = "Image APIs", description = "REST APIs for image operations")
public class ImageController {
    private final ImageService imageService;
    private final ModelMapper modelMapper;

    public ImageController(ImageService imageService, ModelMapper modelMapper) {
        this.imageService = imageService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @Operation(summary = "Get all images", description = "Get a list of all images")
    public ResponseEntity<Object> getAllImages() {
        try {
            List<Image> images = imageService.getAllImages();
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving images: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Upload an image", description = "Upload an image file and return the image details")
    public ResponseEntity<Object> uploadImage(@RequestParam("file") MultipartFile file){
        try {
            Image savedImage = imageService.saveImage(file);
            return ResponseEntity.ok(savedImage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }

    @GetMapping("/{imageId}")
    @Operation(summary = "Get an image", description = "Get an image by its ID")
    public ResponseEntity<Object> getImage(@PathVariable Long imageId) {
        try {
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
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving image: " + e.getMessage());
        }
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete an image", description = "Delete an image by its ID")
    public ResponseEntity<Object> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting image: " + e.getMessage());
        }
    }

    @PostMapping("/search")
    @Operation(summary = "Search images", description = "Search for images based on a query")
    public ResponseEntity<?> searchImages(@RequestParam("file") MultipartFile file){
        try {
            Image savedImage = imageService.saveImage(file);
            float [] queryEmbedding = savedImage.getEmbedding();
            imageService.deleteImage(savedImage.getId());
            List<Image> images = imageService.findTopKSimilarImages(queryEmbedding, 10L);
            List<PostDto> posts = new java.util.ArrayList<>(List.of());
            for (Image image : images) {
                if(image.getPost() != null){
                    posts.add(modelMapper.map(image.getPost(), PostDto.class));
                }
            }
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error searching images: " + e.getMessage());
        }
    }
}