package com.example.lostnfound.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.example.lostnfound.model.User;
import com.example.lostnfound.service.AI.Embedding.EmbeddingService;
import com.example.lostnfound.service.user.UserService;
import org.springframework.stereotype.Service;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;

@Service
public class PostService {
    private final PostRepo postRepo;
    private final EmbeddingService embeddingService;
    private final UserService userService;

    PostService(PostRepo postRepo, EmbeddingService embeddingService, UserService userService) {
        this.postRepo = postRepo;
        this.embeddingService = embeddingService;
        this.userService = userService;
    }

    public Post savePost(Post post) throws IOException, InterruptedException {
        float[] embedding = embeddingService.getEmbedding(post.infoForEmbedding());
        System.out.println("********EmbeddingSize: " + embedding.length);
        post.setEmbedding(embedding);
        User user = userService.findById(post.getUserId());
        user.addInteraction(embedding,3);
        return postRepo.save(post);
    }

    public List<Post> getPosts() {
        return postRepo.findAll();
    }

    public Post getPost(Long id) {
        User currentUser = userService.getCurrentUser();
        currentUser.addInteraction(postRepo.findById(Math.toIntExact(id)).get().getEmbedding(), 1);
        return postRepo.findById(Math.toIntExact(id)).get();
    }

    public void deletePost(int id) {
        postRepo.deleteById(id);
    }

    public Post updatePost(int id, Post post) {
        Post postToUpdate = postRepo.findById(id).get();
            if(Objects.nonNull(postToUpdate.getTitle()) && !"".equalsIgnoreCase(postToUpdate.getTitle())) {
                postToUpdate.setTitle(post.getTitle());
            }
            if(Objects.nonNull(postToUpdate.getDescription()) && !"".equalsIgnoreCase(postToUpdate.getDescription())) {
                postToUpdate.setDescription(post.getDescription());
            }
            if(Objects.nonNull(postToUpdate.getLocation()) && !"".equalsIgnoreCase(postToUpdate.getLocation())) {
                postToUpdate.setLocation(post.getLocation());
            }
            if (post.getDate() != null) {
                postToUpdate.setDate(post.getDate());
            }
            if(postToUpdate.getTime() != null) {
                postToUpdate.setTime(post.getTime());
            }
            if(Objects.nonNull(postToUpdate.getCategory())) {
                postToUpdate.setCategory(post.getCategory());
            }
            if(Objects.nonNull(postToUpdate.getStatus())) {
                postToUpdate.setStatus(post.getStatus());
            }
            if (postToUpdate.getRange() != 0) {
                postToUpdate.setRange(post.getRange());
            }
            postToUpdate.setEmbedding(embeddingService.getEmbedding(postToUpdate.infoForEmbedding()));
            return postRepo.save(postToUpdate);
    }

    public List<Post> searchPosts(String searchTerm) {
        return postRepo.searchPosts(searchTerm);
    }

    private float cosineSimilarity(float[] a, float[] b) {
        float dotProduct = 0;
        float normA = 0;
        float normB = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public List<Post> findTopKSimilarPosts(float[] embed, Long topK) {

        Objects.requireNonNull(embed);
        List<Post> res = postRepo.findTopKSimilarPosts(embed, topK);
        for(Post post : res) {
            System.out.println(post.getId() + " => " + cosineSimilarity(embed, post.getEmbedding()));
        }
        return res;
    }

    public List<Post> getCustomizedPosts() {
        User currentUser = userService.getCurrentUser();
        return findTopKSimilarPosts(currentUser.getEmbedding(), Long.MAX_VALUE);
    }

}
