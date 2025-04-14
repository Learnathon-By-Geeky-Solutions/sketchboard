package com.example.lostnfound.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.model.Image;
import com.example.lostnfound.repository.ImageRepository;
import com.example.lostnfound.service.ImageService;
import com.example.lostnfound.service.PostService;
import com.example.lostnfound.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@Tag(name = "Post APIs", description = "REST APIs for post operations")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final ImageService imageService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ImageRepository imageRepository;

    PostController(PostService postService, UserService userService, ImageService imageService, ObjectMapper objectMapper, ImageRepository imageRepository, ImageRepository imageRepository1) {
        this.postService = postService;
        this.userService = userService;
        this.imageService = imageService;
	    this.imageRepository = imageRepository1;
	    this.modelMapper = new ModelMapper();
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/posts", consumes = {"multipart/form-data"})
    @Operation(summary = "Create a post", description = "Creates a new post with optional image")
    public ResponseEntity<?> handlePost(@RequestParam(value = "postDto", required = true) String postDtoJson,
                                      @RequestParam(value = "image", required = false) MultipartFile image) throws IOException, InterruptedException {
        try {
            PostDto postDto = objectMapper.readValue(postDtoJson, PostDto.class);

            Post newPost = new Post();
            newPost.setUserId(userService.getCurrentUser().getUserId());
            createPostFromDto(postDto, newPost);

            if (image != null && !image.isEmpty()) {
                Image savedImage = imageService.saveImage(image);
                newPost.setImage(savedImage);
                savedImage.setPost(newPost);
            }

            postService.savePost(newPost);
            PostDto responseDto = modelMapper.map(newPost, PostDto.class);
            if (newPost.getImage() != null) {
                responseDto.setImageId(newPost.getImage().getId());
            }
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/posts")
    @Operation(summary = "Get all posts", description = "Retrieves all posts")
    public ResponseEntity<?> getPosts() {
        try {
            List<Post> posts = postService.getPosts();
            List<PostDto> postDtos = new ArrayList<>();
            for (Post post : posts) {
                PostDto dto = modelMapper.map(post, PostDto.class);
                if (post.getImage() != null) {
                    dto.setImageId(post.getImage().getId());
                }
                postDtos.add(dto);
            }
            return new ResponseEntity<>(postDtos, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get post by id", description = "Retrieves post by id")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
        try {
            Post post = postService.getPost(id);
            PostDto postDto = modelMapper.map(post, PostDto.class);
            if (post.getImage() != null) {
                postDto.setImageId(post.getImage().getId());
            }
            return new ResponseEntity<>(postDto, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete post by id", description = "Deletes post by id")
    public ResponseEntity<?> deletePost(@PathVariable("id") int id) {
        try {
            postService.deletePost(id);
            return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "Update post by id", description = "Updates post by id")
    public ResponseEntity<?> updatePost(@PathVariable("id") int id, 
                                      @RequestParam(value = "postDto") String postDtoJson,
                                      @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            PostDto postDto = objectMapper.readValue(postDtoJson, PostDto.class);
            Post updatedPost = new Post();
            createPostFromDto(postDto, updatedPost);

            if (image != null && !image.isEmpty()) {
                Image savedImage = imageService.saveImage(image);
                updatedPost.setImage(savedImage);
                savedImage.setPost(updatedPost);
                imageRepository.save(savedImage);
            }

            postService.updatePost(id, updatedPost);
            PostDto responseDto = modelMapper.map(updatedPost, PostDto.class);
            if (updatedPost.getImage() != null) {
                responseDto.setImageId(updatedPost.getImage().getId());
            }
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private void createPostFromDto(PostDto postDto, Post newPost) throws UserNotFoundException {
        newPost.setTitle(postDto.getTitle());
        newPost.setDescription(postDto.getDescription());
        newPost.setCategory(postDto.getCategory());
        newPost.setUserId(userService.getCurrentUser().getUserId());
        newPost.setLocation(postDto.getLocation());
        newPost.setRange(postDto.getRange());
        newPost.setTime(postDto.getTime());
        newPost.setDate(postDto.getDate());
        newPost.setUserName(userService.getCurrentUser().getName());
        newPost.setStatus(postDto.getStatus());
    }

    @GetMapping("/customizedPosts")
    @Operation(summary = "Get related posts", description = "Retrieves related posts for user")
    public ResponseEntity<?> getCustomizedPosts() {
        try {
            Long myUserId = userService.getCurrentUser().getUserId();
            List<Post> posts = postService.getCustomizedPosts();
            List<PostDto> myPosts = new ArrayList<>();
            for (Post post : posts) {
                if (Objects.equals(post.getUserId(), myUserId)) {
                    continue;
                }
                PostDto dto = modelMapper.map(post, PostDto.class);
                if (post.getImage() != null) {
                    dto.setImageId(post.getImage().getId());
                }
                myPosts.add(dto);
            }
            return new ResponseEntity<>(myPosts, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
