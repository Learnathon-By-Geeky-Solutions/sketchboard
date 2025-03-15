package com.example.lostnfound.controller;

import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.dto.aiSearchQuery;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.service.AI.Embedding.EmbeddingService;

import com.example.lostnfound.service.AI.GeminiChat.GeminiResponse;
import com.example.lostnfound.service.AI.GeminiChat.QueryExecutor;
import com.example.lostnfound.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class AISearchController {

        private final ModelMapper modelMapper = new ModelMapper();
        private final GeminiResponse geminiResponse;
        private final QueryExecutor queryExecutor;
        private final EmbeddingService embeddingService;
        private final PostService postService;

        AISearchController(GeminiResponse geminiResponse, QueryExecutor queryExecutor, EmbeddingService embeddingService, PostService postService) {
            this.geminiResponse = geminiResponse;
            this.queryExecutor = queryExecutor;
            this.embeddingService = embeddingService;
            this.postService = postService;
        }

        @PostMapping("/basicAISearch")
        @Operation(summary = "Basic AI Search", description = "Searches for posts using AI")
        public ResponseEntity<List<Post>> basicAISearch(@RequestBody String query) {
                String response = geminiResponse.getResponse(query);
                return queryExecutor.executeAISearch(response);
        }

        @PostMapping("/getEmbedding")
        @Operation(summary = "Get Embedding", description = "Get embedding for a given input")
        public float[] getEmbedding(@RequestBody String input) throws Exception {
                return embeddingService.getEmbedding(input);
        }

        @PostMapping("/enhancedSearch")
        @Operation(summary = "Enhanced Search", description = "Searches for posts using AI and returns similar posts")
        public List<PostDto> enhancedSearch(@RequestBody aiSearchQuery query) throws IOException, InterruptedException {
                float[] queryEmbedding = embeddingService.getEmbedding(query.getQuery());
                System.out.println("Query embedding: " + queryEmbedding);
                List<Post> res = postService.findTopKSimilarPosts(queryEmbedding, query.getLimit());
                //use object mapper
                List<PostDto> postDtos = new java.util.ArrayList<>(List.of());
                for (Post p : res) {
                        postDtos.add(modelMapper.map(p, PostDto.class));
                }
                return postDtos;
        }

        @PostMapping("/getSimilarPosts")
        @Operation(summary = "Get Similar Posts", description = "Get similar posts for a given post")
        public List<PostDto> getSimilarPosts(@RequestBody aiSearchQuery query) {
                Post post = postService.getPost(query.getPostId());
                List<Post> res = postService.findTopKSimilarPosts(post.getEmbedding(), query.getLimit());
                //use object mapper
                List<PostDto> postDtos = new java.util.ArrayList<>(List.of());
                for (Post p : res) {
                        postDtos.add(modelMapper.map(p, PostDto.class));
                }
                return postDtos;
        }
}