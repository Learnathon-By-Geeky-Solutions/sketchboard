package com.example.lostnfound.service;

import com.example.lostnfound.model.Image;
import com.example.lostnfound.repository.ImageRepository;
import com.example.lostnfound.service.AI.Embedding.EmbeddingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final Path uploadPath;
    private final EmbeddingService embeddingService;

    public ImageService(ImageRepository imageRepository, @Value("${app.upload.dir}") String uploadDir, EmbeddingService embeddingService) {
        this.imageRepository = imageRepository;
        this.uploadPath = Path.of(uploadDir);
	    this.embeddingService = embeddingService;
	    createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public Image saveImage(MultipartFile file) throws IOException, InterruptedException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        Path targetLocation = uploadPath.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Image image = new Image();
        image.setFileName(fileName);
        image.setContentType(file.getContentType());
        image.setFileSize(file.getSize());
        image.setFilePath(targetLocation.toString());
        imageRepository.save(image);
        image.setEmbedding(embeddingService.getEmbeddingImage(image.getImageUri()));
        imageRepository.save(image);
        return image;
    }

    public Resource loadImage(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        try {
            Path filePath = Path.of(image.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + image.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + image.getFileName(), e);
        }
    }

    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        try {
            Path filePath = Path.of(image.getFilePath());
            Files.deleteIfExists(filePath);
            imageRepository.delete(image);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete image file", e);
        }
    }

	public List<Image> getAllImages() {
        return imageRepository.findAll();
	}

    public List<Image> findTopKSimilarImages(float[] queryEmbedding, Long topK) {
        return imageRepository.findTopKSimilarPosts(queryEmbedding, topK);
    }
}