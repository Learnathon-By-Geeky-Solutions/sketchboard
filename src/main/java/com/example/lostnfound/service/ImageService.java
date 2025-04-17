package com.example.lostnfound.service;

import com.example.lostnfound.exception.ImageNotFoundException;
import com.example.lostnfound.exception.ImageStorageException;
import com.example.lostnfound.exception.StorageInitializationException;
import com.example.lostnfound.model.Image;
import com.example.lostnfound.repository.ImageRepository;
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

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final Path uploadPath;

    public ImageService(ImageRepository imageRepository, @Value("${app.upload.dir}") String uploadDir) {
        this.imageRepository = imageRepository;
        this.uploadPath = Path.of(uploadDir);
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new StorageInitializationException("Could not create upload directory at: " + uploadPath, e);
        }
    }

    public Image saveImage(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        Path targetLocation = uploadPath.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Image image = new Image();
        image.setFileName(fileName);
        image.setContentType(file.getContentType());
        image.setFileSize(file.getSize());
        image.setFilePath(targetLocation.toString());

        return imageRepository.save(image);
    }

    public Resource loadImage(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
        try {
            Path filePath = Path.of(image.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ImageNotFoundException("File not found: " + image.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new ImageStorageException("Invalid file path for image: " + image.getFileName(), e);
        }
    }

    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));
        try {
            Path filePath = Path.of(image.getFilePath());
            Files.deleteIfExists(filePath);
            imageRepository.delete(image);
        } catch (IOException e) {
            throw new ImageStorageException("Could not delete image file: " + image.getFileName(), e);
        }
    }

	public List<Image> getAllImages() {
        return imageRepository.findAll();
	}
}