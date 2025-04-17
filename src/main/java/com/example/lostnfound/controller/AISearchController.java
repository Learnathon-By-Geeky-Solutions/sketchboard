package com.example.lostnfound.controller;

import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.dto.AiSearchQuery;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.service.PostService;
import com.example.lostnfound.service.ai.GeminiChat.GeminiResponse;
import com.example.lostnfound.service.ai.GeminiChat.QueryExecutor;
import com.example.lostnfound.service.ai.embedding.EmbeddingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "AI Search APIs", description = "REST APIs related to AI search for searching posts using AI.")
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
        public ResponseEntity<Object> basicAISearch(@RequestBody String query) {
                try {
                        String response = geminiResponse.getResponse(query);
                        ResponseEntity<List<Post>> result = queryExecutor.executeAISearch(response);
                        List<PostDto> postDtos = result.getBody().stream()
                                .map(post -> modelMapper.map(post, PostDto.class))
                                .toList();
                        return new ResponseEntity<>(postDtos, result.getStatusCode());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                }
        }

        @PostMapping("/getEmbedding")
        @Operation(summary = "Get Embedding", description = "Get embedding for a given input")
        public ResponseEntity<Object> getEmbedding(@RequestBody String input) {
                try {
                        float[] embedding = embeddingService.getEmbedding(input);
                        return new ResponseEntity<>(embedding, HttpStatus.OK);
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                }
        }

        @PostMapping("/enhancedSearch")
        @Operation(summary = "Enhanced Search", description = "Searches for posts using AI and returns similar posts")
        public ResponseEntity<Object> enhancedSearch(@RequestBody AiSearchQuery query){
                try {
                        float[] queryEmbedding = embeddingService.getEmbedding(query.getQuery());
                        List<Post> res = postService.findTopKSimilarPosts(queryEmbedding, query.getLimit());
                        List<PostDto> postDtos = res.stream()
                                .map(post -> modelMapper.map(post, PostDto.class))
                                .toList();
                        return new ResponseEntity<>(postDtos, HttpStatus.OK);
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                }
        }

        @PostMapping("/getSimilarPosts")
        @Operation(summary = "Get Similar Posts", description = "Get similar posts for a given post")
        public ResponseEntity<Object> getSimilarPosts(@RequestBody AiSearchQuery query) {
                try {
                        Post post = postService.getPost(query.getPostId());
                        List<Post> res = postService.findTopKSimilarPosts(post.getEmbedding(), query.getLimit());
                        List<PostDto> postDtos = res.stream()
                                .map(p -> modelMapper.map(p, PostDto.class))
                                .toList();
                        return new ResponseEntity<>(postDtos, HttpStatus.OK);
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
                }
        }
}