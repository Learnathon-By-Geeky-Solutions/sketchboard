package com.example.lostnfound.controller;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.service.basicai.GeminiResponse;
import com.example.lostnfound.service.basicai.QueryExecutor;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AISearchController {

        private final GeminiResponse geminiResponse;
        private final QueryExecutor queryExecutor;

        AISearchController(GeminiResponse geminiResponse, QueryExecutor queryExecutor) {
                this.geminiResponse = geminiResponse;
                this.queryExecutor = queryExecutor;
        }

        @GetMapping("/basicAISearch")
        @Operation(summary = "Basic AI Search", description = "Searches for posts using AI")
        public ResponseEntity<List<Post>> basicAISearch(@RequestParam ("msg") String query) {
                String response = geminiResponse.getResponse(query);
                return queryExecutor.executeAISearch(response);
        }

}