package com.example.lostnfound.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import jakarta.annotation.PostConstruct;

import java.io.IOException;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AISearchController {
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

        @GetMapping("/basicAISearch")
        public ResponseEntity<String> getResponse(@RequestParam("message") String query) {
            if (StringUtils.isBlank(query)) {
                return ResponseEntity.badRequest().body("Query parameter cannot be empty");
            }
            if (query.length() > 1000) {
                return ResponseEntity.badRequest().body("Query exceeds maximum length of 1000 characters");
            }
            // Sanitize input
            query = query.replaceAll("[<>]", "")
                         .replaceAll("(?i)script", "")
                         .trim();
            try {
                GenerateContentResponse response =
                        client.models.generateContent(modelId, query, null);
                return ResponseEntity.ok(response.text());
            } catch (HttpException | IOException e) {
                log.error("Error processing AI search request", e);
                String errorCode = "AI_ERR_" + System.currentTimeMillis();
                log.error("Error code: {} - {}", errorCode, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing request. Reference: " + errorCode);
            }
    }
}