package com.example.lostnfound.controller;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.service.BasicAISearch.GeminiResponse;
import com.example.lostnfound.service.BasicAISearch.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AISearchController {

        @Autowired
        private GeminiResponse geminiResponse;

        @Autowired
        private QueryExecutor queryExecutor;

        @GetMapping("/basicAISearch")
        public ResponseEntity<List<Post>> basicAISearch(@RequestParam ("msg") String query) {
                String response = geminiResponse.getResponse(query);
                return queryExecutor.executeAISearch(response);
        }

}