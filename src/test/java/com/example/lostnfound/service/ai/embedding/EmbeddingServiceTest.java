package com.example.lostnfound.service.ai.embedding;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmbeddingServiceTest {

    private EmbeddingService embeddingService;
    private MockedStatic<HttpClient> httpClientStatic;
    private HttpClient clientMock;
    private HttpResponse<String> responseMock;

    @BeforeEach
    void setUp() {
        // instantiate and inject aiLabUrl
        embeddingService = new EmbeddingService();
        ReflectionTestUtils.setField(embeddingService, "aiLabUrl", "http://localhost");
        // mock HttpClient.newHttpClient()
        clientMock = mock(HttpClient.class);
        responseMock = mock(HttpResponse.class);
        httpClientStatic = mockStatic(HttpClient.class);
        httpClientStatic.when(HttpClient::newHttpClient).thenReturn(clientMock);
    }

    @AfterEach
    void tearDown() {
        httpClientStatic.close();
    }

    @Test
    void testGetEmbeddingClipLocal_Text_Success() throws Exception {
        // Arrange
        String input = "hello";
        when(responseMock.body()).thenReturn("[[0.5,1.5]]");
        when(clientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(responseMock);

        // Act
        float[] result = embeddingService.getEmbeddingClipLocal(input, 0);

        // Assert
        assertArrayEquals(new float[]{0.5f, 1.5f}, result, 1e-6f);

        // verify request
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(clientMock).send(captor.capture(), any());
        HttpRequest req = captor.getValue();
        assertEquals("POST", req.method());
        assertEquals(URI.create("http://localhost/encode"), req.uri());
        assertEquals("application/json", req.headers().firstValue("Content-Type").orElse(""));
    }

    @Test
    void testGetEmbeddingClipLocal_Image_Success() throws Exception {
        // Arrange
        when(responseMock.body()).thenReturn("[[2.0,3.0,4.0]]");
        when(clientMock.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(responseMock);

        // Act
        float[] result = embeddingService.getEmbeddingClipLocal("img_uri", 1);
        assertArrayEquals(new float[]{2.0f, 3.0f, 4.0f}, result, 1e-6f);
    }


    @Test
    void testGetEmbeddingClipLocal_IOException() throws Exception {
        when(clientMock.send(any(), any())).thenThrow(new IOException("IO fail"));
        assertThrows(IOException.class, () -> embeddingService.getEmbeddingClipLocal("x", 0));
    }

    @Test
    void testGetEmbeddingClipLocal_InterruptedException() throws Exception {
        when(clientMock.send(any(), any())).thenThrow(new InterruptedException("Interrupted"));
        assertThrows(InterruptedException.class, () -> embeddingService.getEmbeddingClipLocal("y", 1));
    }




}
