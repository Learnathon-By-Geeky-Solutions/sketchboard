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
import org.springframework.stereotype.Service;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;

/**
 * Service class for managing posts in the application.
 * Provides methods for creating, retrieving, updating, deleting, and searching posts.
 * Also includes functionality for embedding generation, similarity search, and user interaction tracking.
 */
@Service
public class PostService {

    /**
     * Constructor for PostService.
     *
     * @param postRepo          Repository for managing Post entities.
     * @param embeddingService  Service for generating embeddings for posts.
     * @param userService       Service for managing user-related operations.
     * @param imageService      Service for managing image-related operations.
     */
    PostService(PostRepo postRepo, EmbeddingService embeddingService, UserService userService, 
                ImageService imageService) { }

    /**
     * Saves a new post to the repository after generating its embedding and associating it with a user.
     *
     * @param post The post to be saved.
     * @throws IOException              If an I/O error occurs during embedding generation.
     * @throws InterruptedException     If the embedding generation process is interrupted.
     * @throws UserNotFoundException    If the user associated with the post is not found.
     */
    public void savePost(Post post) throws IOException, InterruptedException, UserNotFoundException { }

    /**
     * Retrieves all posts from the repository.
     *
     * @return A list of all posts.
     */
    public List<Post> getPosts() { }

    /**
     * Retrieves posts with pagination support.
     *
     * @param pageNo   The page number to retrieve.
     * @param pageSize The number of posts per page.
     * @return A paginated list of posts.
     */
    public Page<Post> getPostsWithPagination(int pageNo, int pageSize) { }

    /**
     * Retrieves a specific post by its ID and updates the current user's interaction with it.
     *
     * @param id The ID of the post to retrieve.
     * @return The retrieved post.
     * @throws UserNotFoundException If the current user is not found.
     */
    public Post getPost(Long id) throws UserNotFoundException { }

    /**
     * Deletes a post by its ID, including its associated image if present.
     *
     * @param id The ID of the post to delete.
     */
    public void deletePost(int id) { }

    /**
     * Updates an existing post with new information.
     *
     * @param id   The ID of the post to update.
     * @param post The updated post information.
     */
    public void updatePost(int id, Post post) { }

    /**
     * Searches for posts based on a search term.
     *
     * @param searchTerm The term to search for.
     * @return A list of posts matching the search term.
     */
    public List<Post> searchPosts(String searchTerm) { }

    /**
     * Calculates the cosine similarity between two embedding vectors.
     *
     * @param a The first embedding vector.
     * @param b The second embedding vector.
     * @return The cosine similarity between the two vectors.
     */
    private float cosineSimilarity(float[] a, float[] b) { }

    /**
     * Finds the top K posts most similar to a given embedding.
     *
     * @param embed The embedding to compare against.
     * @param topK  The number of top similar posts to retrieve.
     * @return A list of the top K similar posts.
     */
    public List<Post> findTopKSimilarPosts(float[] embed, Long topK) { }

    /**
     * Retrieves customized posts for the current user based on their embedding.
     *
     * @return A list of posts customized for the current user.
     * @throws UserNotFoundException If the current user is not found.
     */
    public List<Post> getCustomizedPosts() throws UserNotFoundException { }

    /**
     * Updates the image of a post if it has changed.
     *
     * @param newPost      The new post containing the updated image.
     * @param existingPost The existing post to update.
     */
    private void updateImageIfChanged(Post newPost, Post existingPost) { }

    /**
     * Updates a field of a post if the new value is present and non-empty.
     *
     * @param fieldValue The new value for the field.
     * @param setter     The setter method to update the field.
     */
    private void updateFieldIfPresent(String fieldValue, Consumer<String> setter) { }
}

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
