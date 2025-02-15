package com.example.lostnfound.service.BasicAISearch;

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

    private Constants constants = new Constants();

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


        // Adding header footer to query
        query = constants.HeaderMsgForGemini + " user message: " + query + "\n";
        query = query + " DB table info:\n" + constants.TableInfo + "\n";
        query = query + constants.FooterMsgForGemini;

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
