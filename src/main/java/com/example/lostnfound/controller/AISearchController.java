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
        private String geminiApiKey;
        @PostConstruct
        private void initializeClient() {
            client = Client.builder().apiKey(geminiApiKey).build();
        }

        @GetMapping("/basicAISearch")
        public ResponseEntity<String> getResponse(@RequestParam("message") String query) {
            if (StringUtils.isBlank(query)) {
                return ResponseEntity.badRequest().body("Query parameter cannot be empty");
            }
            if (query.length() > 1000) {
                return ResponseEntity.badRequest().body("Query exceeds maximum length of 1000 characters");
            }
            try {
                GenerateContentResponse response =
                        client.models.generateContent("gemini-2.0-flash-001", query, null);
                return ResponseEntity.ok(response.text());
            } catch (HttpException | IOException e) {
                log.error("Error processing AI search request", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing request");
            }
    }
}