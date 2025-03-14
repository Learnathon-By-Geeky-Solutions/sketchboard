package com.example.lostnfound.service.post;

import java.io.IOException;
import java.util.List;

import com.example.lostnfound.model.Post;

public interface PostService {

    Post savePost(Post post) throws IOException, InterruptedException;

    List<Post> getPosts();

    Post getPost(int id);

    void deletePost(int id);

    Post updatePost(int id, Post post);

    List<Post> searchPosts(String searchTerm) ;

    List<Post> findTopKSimilarPosts(float[] embed, int topK);

    List<Post> getCustomizedPosts();
}
