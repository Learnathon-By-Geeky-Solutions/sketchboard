package com.example.lostnfound.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.model.UserProfileResponse;
import com.example.lostnfound.service.post.PostService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Post postMethodName(@RequestBody Post post) {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }
        if (email != null) {
            User user = userService.findByEmail(email);
            post.setUser(user);
        } else {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        return postService.savePost(post);
    }

    @GetMapping("/posts")
    public List<Post> getPosts() {
        return postService.getPosts();
    }
    
    @GetMapping("/posts/{id}")
    public Post getPost(@PathVariable("id") int id) {
        return postService.getPost(id);
    }

    @DeleteMapping("/posts/{id}")
    public String deletePost(@PathVariable("id") int id) {
        postService.deletePost(id);
        return "Post deleted successfully";
    }

    @PutMapping("posts/{id}")
    public Post updatePost(@PathVariable("id") int id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    @GetMapping("/search-posts")
    public List<Post> searchPosts(@RequestParam String query) {
        return postService.searchPosts(query);
    }

    @GetMapping("/categories")
    public Catagory[] getAllCategories() {
        return Catagory.getAllCategories();
    }
}
