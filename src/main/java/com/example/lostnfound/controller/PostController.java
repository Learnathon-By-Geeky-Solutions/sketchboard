package com.example.lostnfound.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.service.PostService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/posts")
    public Post postMethodName(@RequestBody Post post) {
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


}
