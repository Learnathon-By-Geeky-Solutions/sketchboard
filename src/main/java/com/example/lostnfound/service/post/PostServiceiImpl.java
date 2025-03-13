package com.example.lostnfound.service.post;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.example.lostnfound.service.AI.Embedding.EmbeddingService;
import org.springframework.stereotype.Service;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;

@Service
public class PostServiceiImpl implements PostService {
    private final PostRepo postRepo;
    private final EmbeddingService embeddingService;

    PostServiceiImpl(PostRepo postRepo, EmbeddingService embeddingService) {
        this.postRepo = postRepo;
        this.embeddingService = embeddingService;
    }

    @Override
    public Post savePost(Post post) throws IOException, InterruptedException {
        float[] embedding = embeddingService.getEmbedding(post.infoForEmbedding());
        System.out.println("********EmbeddingSize: " + embedding.length);
        post.setEmbedding(embedding);
        return postRepo.save(post);
    }

    @Override
    public List<Post> getPosts() {
        return postRepo.findAll();
    }

    @Override
    public Post getPost(int id) {
        return postRepo.findById(id).get();
    }

    @Override
    public void deletePost(int id) {
        postRepo.deleteById(id);
    }

    @Override
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
            
            return postRepo.save(postToUpdate);
    }

    @Override
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

    @Override
    public List<Post> findTopKSimilarPosts(float[] embed, int topK) {

        Objects.requireNonNull(embed);
        List<Post> res = postRepo.findTopKSimilarPosts(embed, topK);
        for(Post post : res) {
            System.out.println(post.getId() + " => " + cosineSimilarity(embed, post.getEmbedding()));
        }
        return res;
    }
}
