package com.example.lostnfound.service;

import com.example.lostnfound.exception.ImageNotFoundException;
import com.example.lostnfound.exception.ImageStorageException;
import com.example.lostnfound.exception.StorageInitializationException;
import com.example.lostnfound.model.Image;
import com.example.lostnfound.repository.ImageRepository;
import com.example.lostnfound.service.ai.embedding.EmbeddingService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private EmbeddingService embeddingService;

    private ImageService imageService;
    private Path uploadDir;
    private MockedStatic<Files> filesMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup a dummy upload directory (no actual file system interaction)
        uploadDir = Path.of("dummyDir");

        // Mock static Files methods
        filesMock = Mockito.mockStatic(Files.class);
        filesMock.when(() -> Files.createDirectories(any(Path.class))).thenReturn(uploadDir);
        filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class))).thenReturn(0L);
        filesMock.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);

        // Initialize service with mocked dependencies
        imageService = new ImageService(imageRepository, uploadDir.toString(), embeddingService);
    }

    @AfterEach
    void tearDown() {
        filesMock.close();
    }

    @Test
    void testSaveImage() throws IOException, InterruptedException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test image content".getBytes());
        Image savedImage = new Image();
        savedImage.setId(1L);
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> {
            Image img = invocation.getArgument(0);
            img.setId(1L);
            return img;
        });
        when(embeddingService.getEmbeddingImage(anyString())).thenReturn(new float[]{0.1f, 0.2f, 0.3f});

        // When
        Image result = imageService.saveImage(file);

        // Then
        assertNotNull(result);
        assertTrue(result.getFileName().matches("\\d+_[a-f0-9\\-]+\\.jpg"));
        verify(imageRepository, times(2)).save(any(Image.class));
    }

    @Test
    void testSaveImage_InvalidExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "image.txt", "text/plain", "content".getBytes());
        assertThrows(IllegalArgumentException.class, () -> imageService.saveImage(file));
    }

    @Test
    void testLoadImage_Success() throws IOException {
        // Given
        Image img = new Image();
        img.setId(1L);
        img.setFileName("test.jpg");
        img.setFilePath(uploadDir.resolve("test.jpg").toString());
        when(imageRepository.findById(1L)).thenReturn(Optional.of(img));

        // Mock UrlResource construction to avoid real file system
        try (MockedConstruction<UrlResource> mocked = mockConstruction(UrlResource.class, (mock, ctx) -> {
            when(mock.exists()).thenReturn(true);
        })) {
            // When
            Resource resource = imageService.loadImage(1L);
            // Then
            assertNotNull(resource);
            assertTrue(resource.exists());
        }
    }

    @Test
    void testLoadImage_NotFoundInRepo() {
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ImageNotFoundException.class, () -> imageService.loadImage(1L));
    }

    @Test
    void testLoadImage_FileNotExists() {
        Image img = new Image();
        img.setId(2L);
        img.setFileName("nofile.jpg");
        img.setFilePath(uploadDir.resolve("nofile.jpg").toString());
        when(imageRepository.findById(2L)).thenReturn(Optional.of(img));

        // Mock UrlResource to indicate non-existence
        try (MockedConstruction<UrlResource> mocked = mockConstruction(UrlResource.class, (mock, ctx) -> {
            when(mock.exists()).thenReturn(false);
        })) {
            assertThrows(ImageNotFoundException.class, () -> imageService.loadImage(2L));
        }
    }

    @Test
    void testDeleteImage_Success() {
        // Given
        Image img = new Image();
        img.setId(1L);
        img.setFilePath(uploadDir.resolve("toDelete.jpg").toString());
        when(imageRepository.findById(1L)).thenReturn(Optional.of(img));

        // When
        imageService.deleteImage(1L);

        // Then
        verify(imageRepository, times(1)).delete(img);
        filesMock.verify(() -> Files.deleteIfExists(any(Path.class)));
    }

    @Test
    void testDeleteImage_NotFound() {
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ImageNotFoundException.class, () -> imageService.deleteImage(1L));
    }

    @Test
    void testSimilarityScore() {
        float[] a = {1.0f, 2.0f, 3.0f};
        float[] b = {4.0f, 5.0f, 6.0f};
        float expected = (float) Math.sqrt(27);
        assertEquals(expected, imageService.similarityScore(a,b), 1e-6);
    }

    @Test
    void testFindTopKSimilarImages() {
        float[] query = {0.1f, 0.2f, 0.3f};
        Image i1 = new Image(), i2 = new Image();
        List<Image> list = List.of(i1, i2);
        when(imageRepository.findTopKSimilarPosts(query, 2L)).thenReturn(list);

        var result = imageService.findTopKSimilarImages(query, 2L);
        assertEquals(2, result.size());
        assertSame(list, result);
    }
}
