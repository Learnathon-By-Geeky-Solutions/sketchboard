package com.example.lostnfound.service.ai.geminichat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;


class GeminiResponseTest {

    @InjectMocks
    private GeminiResponse geminiResponse;  // Inject the mocked Client into GeminiResponse

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set mock values directly on GeminiResponse object
        geminiResponse = new GeminiResponse();
        geminiResponse.geminiApiKey = "mock-api-key".toCharArray();
        geminiResponse.modelId = "gemini-pro";
    }



    @Test
    void testGetResponse_emptyQuery_returnsErrorMessage() {
        // Simulate an empty query input
        String response = geminiResponse.getResponse("   ");
        assertEquals("Query parameter cannot be empty", response);
    }

    @Test
    void testGetResponse_longQuery_returnsErrorMessage() {
        // Simulate a long query that exceeds the max length
        String longQuery = "a".repeat(501);  // 501 characters
        String response = geminiResponse.getResponse(longQuery);
        assertEquals("Query exceeds maximum length of 500 characters", response);
    }




}
