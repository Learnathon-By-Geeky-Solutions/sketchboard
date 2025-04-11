package com.example.lostnfound.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.service.PostService;
import com.example.lostnfound.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.enums.Category;
import com.example.lostnfound.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Post APIs", description = "REST APIs related to posts for CRUD operations.Like create, read, update, delete and search posts.")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    
    @Value("${app.upload.dir}")
    private String uploadDir;

    PostController(PostService postService, UserService userService, ObjectMapper objectMapper) {
        this.postService = postService;
        this.userService = userService;
        this.modelMapper = new ModelMapper();
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/posts", consumes = {"multipart/form-data"})
    @Operation(summary = "Create or update a post", description = "Handles post creation or update with optional image upload")
    public ResponseEntity<?> handlePost(@RequestParam(value = "postDto", required = true) String postDtoJson,
                                        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException, InterruptedException {
        try {
            PostDto postDto = objectMapper.readValue(postDtoJson, PostDto.class);

            Post newPost = new Post();
            newPost.setUserId(userService.getCurrentUser().getUserId());
            createPostFromDto(postDto, newPost);

            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image);
                newPost.setImagePath(imagePath);
            }

            postService.savePost(newPost);
            return new ResponseEntity<>(modelMapper.map(newPost, PostDto.class), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path uploadPath = Path.of(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        Path targetLocation = uploadPath.resolve(uniqueFilename);
        
        try {
            Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new IOException("Could not store file " + uniqueFilename + ". Please try again!", ex);
        }
    }

    private void createPostFromDto(@RequestBody PostDto postDto, Post newPost) throws UserNotFoundException {
        newPost.setTitle(postDto.getTitle());
        newPost.setDescription(postDto.getDescription());
        newPost.setCategory(postDto.getCategory());
        newPost.setUserId(userService.getCurrentUser().getUserId());
        newPost.setLocation(postDto.getLocation());
        newPost.setRange(postDto.getRange());
        newPost.setTime(postDto.getTime());
        newPost.setDate(postDto.getDate());
        newPost.setUserName(userService.getCurrentUser().getName()); // Automatically assign user name
        newPost.setStatus(postDto.getStatus());
    }

    @GetMapping("/posts")
    @Operation(summary = "Get all posts", description = "Retrieves all posts")
    public ResponseEntity<?> getPosts() {
        try {
            List<Post> posts = postService.getPosts();
            List<PostDto> myPosts = new ArrayList<>();
            for (Post post : posts) {
                myPosts.add(modelMapper.map(post, PostDto.class));
            }
            return new ResponseEntity<>(myPosts, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/customizedPosts")
    @Operation(summary = "Get related posts", description = "Retrieves related posts for user")
    public ResponseEntity<?> getCustomizedPosts() {
        try {
            Long myUserId = userService.getCurrentUser().getUserId();
            List<Post> posts = postService.getCustomizedPosts();
            List<PostDto> myPosts = new ArrayList<>();
            for (Post post : posts) {
                //skip if post is by the user
                if (Objects.equals(post.getUserId(), myUserId)) {
                    continue;
                }
                myPosts.add(modelMapper.map(post, PostDto.class));
            }
            return new ResponseEntity<>(myPosts, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get post by id", description = "Retrieves post by id")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
        try {
            Post post = postService.getPost(id);
            PostDto myPost = modelMapper.map(post, PostDto.class);
            return new ResponseEntity<>(myPost, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete post by id", description = "Deletes post by id")
    public ResponseEntity<String> deletePost(@PathVariable("id") int id) {
        try {
            postService.deletePost(id);
            return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("posts/{id}")
    @Operation(summary = "Update post by id", description = "Updates post by id")
    public ResponseEntity<?> updatePost(@PathVariable("id") int id, @RequestBody PostDto postDto) {
        try{
            Post updatedPost = new Post();
            createPostFromDto(postDto, updatedPost);
            postService.updatePost(id, updatedPost);
            PostDto myPost = modelMapper.map(updatedPost, PostDto.class);
            return new ResponseEntity<>(myPost, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search posts", description = "Searches posts by query")
    public ResponseEntity<?> searchPosts(@RequestParam("q") String query) {
        try {
            List<Post> posts = postService.searchPosts(query);
            List<PostDto> myPosts = new ArrayList<>();
            for (Post post : posts) {
                myPosts.add(modelMapper.map(post, PostDto.class));
            }
            return new ResponseEntity<>(myPosts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieves all categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            Category[] categories = Category.getAllCategories();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
