package com.example.lostnfound.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.function.Consumer;

import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.model.User;
import com.example.lostnfound.service.ai.embedding.EmbeddingService;
import com.example.lostnfound.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;

@Service
public class PostService {
    private final PostRepo postRepo;
    private final EmbeddingService embeddingService;
    private final UserService userService;
    private final ImageService imageService;

    private Logger logger = Logger.getLogger(PostService.class.getName());

    PostService(PostRepo postRepo, EmbeddingService embeddingService, UserService userService, 
               ImageService imageService) {
        this.postRepo = postRepo;
        this.embeddingService = embeddingService;
        this.userService = userService;
        this.imageService = imageService;
    }

    public void savePost(Post post) throws IOException, InterruptedException, UserNotFoundException {
        float[] embedding = embeddingService.getEmbedding(post.infoForEmbedding());
        logger.info("********EmbeddingSize: " + embedding.length);
        post.setEmbedding(embedding);
        User user = userService.findById(post.getUserId());
        user.addInteraction(embedding,3);
        post.setUserName(user.getName());
        postRepo.save(post);
    }

    public List<Post> getPosts() {
        return postRepo.findAll();
    }

    public Page<Post> getPostsWithPagination(int pageNo, int pageSize) {
        return postRepo.findAll(PageRequest.of(pageNo, pageSize).withSort(Sort.by("id")));
    }

    public Post getPost(Long id) throws UserNotFoundException {
        User currentUser = userService.getCurrentUser();
        Post post = postRepo.findById(id)
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
    
        updateImageIfChanged(post, postToUpdate);
        updateFieldIfPresent(post.getTitle(), postToUpdate::setTitle);
        updateFieldIfPresent(post.getDescription(), postToUpdate::setDescription);
        updateFieldIfPresent(post.getLocation(), postToUpdate::setLocation);
    
        if (post.getDate() != null) {
            postToUpdate.setDate(post.getDate());
        }
    
        if (post.getTime() != null) {
            postToUpdate.setTime(post.getTime());
        }
    
        if (post.getCategory() != null) {
            postToUpdate.setCategory(post.getCategory());
        }
    
        if (post.getStatus() != null) {
            postToUpdate.setStatus(post.getStatus());
        }
    
        if (post.getRange() != 0) {
            postToUpdate.setRange(post.getRange());
        }
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
            logger.info(post.getId() + " => " + cosineSimilarity(embed, post.getEmbedding()));
        }
        return res;
    }

    public List<Post> getCustomizedPosts() throws UserNotFoundException {
        User currentUser = userService.getCurrentUser();
        return findTopKSimilarPosts(currentUser.getEmbedding(), Long.MAX_VALUE);
    }

    private void updateImageIfChanged(Post newPost, Post existingPost) {
        if (newPost.getImage() != null &&
            !Objects.equals(newPost.getImage().getId(),
                existingPost.getImage() != null ? existingPost.getImage().getId() : null)) {
    
            if (existingPost.getImage() != null) {
                imageService.deleteImage(existingPost.getImage().getId());
            }
    
            existingPost.setImage(newPost.getImage());
        }
    }
    
    private void updateFieldIfPresent(String fieldValue, Consumer<String> setter) {
        if (fieldValue != null && !fieldValue.trim().isEmpty()) {
            setter.accept(fieldValue);
        }
    }
    

}
