package com.example.lostnfound.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.example.lostnfound.exception.UserNotFoundException;
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
    private final ImageService imageService;

    PostService(PostRepo postRepo, EmbeddingService embeddingService, UserService userService, 
               ImageService imageService) {
        this.postRepo = postRepo;
        this.embeddingService = embeddingService;
        this.userService = userService;
        this.imageService = imageService;
    }

    public void savePost(Post post) throws IOException, InterruptedException, UserNotFoundException {
        float[] embedding = embeddingService.getEmbedding(post.infoForEmbedding());
        System.out.println("********EmbeddingSize: " + embedding.length);
        post.setEmbedding(embedding);
        User user = userService.findById(post.getUserId());
        user.addInteraction(embedding,3);
        post.setUserName(user.getName());
        postRepo.save(post);
    }

    public List<Post> getPosts() {
        return postRepo.findAll();
    }

    public Post getPost(Long id) throws UserNotFoundException {
        User currentUser = userService.getCurrentUser();
        Post post = postRepo.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Post not found"));
        currentUser.addInteraction(post.getEmbedding(), 1);
        return post;
    }

    public void deletePost(int id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (post.getImage() != null) {
            imageService.deleteImage(post.getImage().getId());
        }
        
        postRepo.deleteById(id);
    }

    public void updatePost(int id, Post post) {
        Post postToUpdate = postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getImage() != null && !Objects.equals(post.getImage().getId(), postToUpdate.getImage() != null ? postToUpdate.getImage().getId() : null)) {
            // Delete old image if exists
            if (postToUpdate.getImage() != null) {
                imageService.deleteImage(postToUpdate.getImage().getId());
            }
            // Set new image
            postToUpdate.setImage(post.getImage());
        }

        if(Objects.nonNull(post.getTitle()) && !"".equalsIgnoreCase(post.getTitle())) {
            postToUpdate.setTitle(post.getTitle());
        }
        if(Objects.nonNull(post.getDescription()) && !"".equalsIgnoreCase(post.getDescription())) {
            postToUpdate.setDescription(post.getDescription());
        }
        if(Objects.nonNull(post.getLocation()) && !"".equalsIgnoreCase(post.getLocation())) {
            postToUpdate.setLocation(post.getLocation());
        }
        if (post.getDate() != null) {
            postToUpdate.setDate(post.getDate());
        }
        if(post.getTime() != null) {
            postToUpdate.setTime(post.getTime());
        }
        if(Objects.nonNull(post.getCategory())) {
            postToUpdate.setCategory(post.getCategory());
        }
        if(Objects.nonNull(post.getStatus())) {
            postToUpdate.setStatus(post.getStatus());
        }
        if (post.getRange() != 0) {
            postToUpdate.setRange(post.getRange());
        }

	    postToUpdate.setEmbedding(embeddingService.getEmbedding(postToUpdate.infoForEmbedding()));
	    postRepo.save(postToUpdate);
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

    public List<Post> getCustomizedPosts() throws UserNotFoundException {
        User currentUser = userService.getCurrentUser();
        return findTopKSimilarPosts(currentUser.getEmbedding(), Long.MAX_VALUE);
    }

}
