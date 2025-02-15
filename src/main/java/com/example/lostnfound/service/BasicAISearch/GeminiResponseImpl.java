package com.example.lostnfound.service.BasicAISearch;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.util.DatabaseUtils;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@Component
public class GeminiResponseImpl implements GeminiResponse {
    private Client client;
    @Value("${gemini.api.key}")
    private char[] geminiApiKey;
    @Value("${gemini.model.id}")
    private String modelId;

    @PostConstruct
    private void initializeClient() {
        try {
            client = Client.builder().apiKey(new String(geminiApiKey)).build();
            // Clear sensitive data from memory
            java.util.Arrays.fill(geminiApiKey, '\0');
        } catch (Exception e) {
            log.error("Failed to initialize AI client", e);
            throw new RuntimeException("AI service initialization failed");
        }
    }
    @Override
    public String getResponse(String query) {
        if (StringUtils.isBlank(query)) {
            return "Query parameter cannot be empty";
        }
        if (query.length() > 500) {
            return "Query exceeds maximum length of 500 characters";
        }
        // Sanitize input
        query = query.replaceAll("[<>]", "")
                .replaceAll("(?i)script", "")
                .trim();

        // Get table and column names from Post class
        Post post = new Post();
        // Header message
        String header = "Hey Gemini, Read the below scenario and my database information and write the best possible SQL query (search item in category/title/description 'lost is not an item, it's a verb'). Please only write the SQL query, don't write any other text.\n";
        // Footer
        String tableAndColumnNames = DatabaseUtils.getTableAndColumnNames(Post.class);

        // Adding header footer to query
        query = header + " user message: " + query + "\n";
        query = query + " table info:\n" + tableAndColumnNames + "\n";
        query = query + "Don't use hard query. Status should be 'Found' always. not lost";

        final String responseFromGemini;

        try {
            GenerateContentResponse response =
                    client.models.generateContent(modelId, query, null);
            responseFromGemini = response.text();
        } catch (HttpException | IOException e) {
            log.error("Error processing AI search request", e);
            return "AI_ERR_" + System.currentTimeMillis();
        }
        return responseFromGemini;
    }
}
