package com.example.lostnfound.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.service.post.PostService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.lostnfound.enums.Catagory;
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

    PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/posts")
    @Operation(summary = "Create a new post", description = "Creates a new post")
    public ResponseEntity<Post> postMethodName(@RequestBody Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }
        if (email != null) {
            User user = userService.findByEmail(email);
            post.setUser(user);
        } else {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        Post savedPost = postService.savePost(post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @GetMapping("/posts")
    @Operation(summary = "Get all posts", description = "Retrieves all posts")
    public ResponseEntity<List<Post>> getPosts() {
        List<Post> posts = postService.getPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
    
    @GetMapping("/posts/{id}")
    @Operation(summary = "Get post by id", description = "Retrieves post by id")
    public ResponseEntity<Post> getPost(@PathVariable("id") int id) {
        Post post = postService.getPost(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete post by id", description = "Deletes post by id")
    public ResponseEntity<String> deletePost(@PathVariable("id") int id) {
        postService.deletePost(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }

    @PutMapping("posts/{id}")
    @Operation(summary = "Update post by id", description = "Updates post by id")
    public ResponseEntity<Post> updatePost(@PathVariable("id") int id, @RequestBody Post post) {
        Post updatedPost = postService.updatePost(id, post);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @GetMapping("/search-posts")
    @Operation(summary = "Search posts", description = "Searches posts by query")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String query) {
        List<Post> posts = postService.searchPosts(query);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieves all categories")
    public ResponseEntity<Catagory[]> getAllCategories() {
        Catagory[] categories = Catagory.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}
