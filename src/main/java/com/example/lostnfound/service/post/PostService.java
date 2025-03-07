package com.example.lostnfound.service.post;

import java.util.List;

import com.example.lostnfound.model.Post;

public interface PostService {

    Post savePost(Post post);

    List<Post> getPosts();

    Post getPost(int id);

    void deletePost(int id);

    Post updatePost(int id, Post post);

    List<Post> searchPosts(String searchTerm) ;
    
} 
