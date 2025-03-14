package com.example.lostnfound.controller;

import com.example.lostnfound.dto.PostDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.service.post.PostService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.lostnfound.enums.Category;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.lostnfound.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;

import com.example.lostnfound.exception.UserNotAuthenticatedException;

@RestController
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/posts")
    @Operation(summary = "Create a new post", description = "Creates a new post")
    public ResponseEntity<PostDto> postMethodName(@RequestBody Post post) throws IOException, InterruptedException {
        post.setUserId(userService.getCurrentUser().getUserId());
        Post savedPost = postService.savePost(post);
        PostDto myPost = modelMapper.map(savedPost, PostDto.class);
        return new ResponseEntity<>(myPost, HttpStatus.CREATED);
    }

    @GetMapping("/posts")
    @Operation(summary = "Get all posts", description = "Retrieves all posts")
    public ResponseEntity<List<PostDto>> getPosts() {
        List<Post> posts = postService.getPosts();
        List<PostDto>myPosts = new ArrayList<>();
        for(Post post: posts){
            myPosts.add(modelMapper.map(post, PostDto.class));
        }
        return new ResponseEntity<>(myPosts, HttpStatus.OK);
    }

    @GetMapping("/customizedPosts")
    @Operation(summary = "Get related posts", description = "Retrieves related posts for user")
    public  ResponseEntity<List<PostDto>> getCustomizedPosts() {
        Long myUserId = userService.getCurrentUser().getUserId();
        List<Post> posts = postService.getCustomizedPosts();
        List<PostDto>myPosts = new ArrayList<>();
        for(Post post: posts){
            //skip if post is by the user
            if(Objects.equals(post.getUserId(), myUserId)){
                continue;
            }
            myPosts.add(modelMapper.map(post, PostDto.class));
        }
        return new ResponseEntity<>(myPosts, HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get post by id", description = "Retrieves post by id")
    public ResponseEntity<PostDto> getPost(@PathVariable("id") int id) {
        Post post = postService.getPost(id);
        PostDto myPost = modelMapper.map(post, PostDto.class);
        System.out.println("The last update time: " + post.getLastUpdatedTime());
        return new ResponseEntity<>(myPost, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete post by id", description = "Deletes post by id")
    public ResponseEntity<String> deletePost(@PathVariable("id") int id) {
        postService.deletePost(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }

    @PutMapping("posts/{id}")
    @Operation(summary = "Update post by id", description = "Updates post by id")
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") int id, @RequestBody Post post) {
        Post updatedPost = postService.updatePost(id, post);
        PostDto myPost = modelMapper.map(updatedPost, PostDto.class);
        return new ResponseEntity<>(myPost, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Search posts", description = "Searches posts by query")
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam("q") String query) {
        List<Post> posts = postService.searchPosts(query);
        List<PostDto> myPosts = new ArrayList<>();
        for (Post post : posts) {
            myPosts.add(modelMapper.map(post, PostDto.class));
        }
        return new ResponseEntity<>(myPosts, HttpStatus.OK);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieves all categories")
    public ResponseEntity<Category[]> getAllCategories() {
        Category[] categories = Category.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
