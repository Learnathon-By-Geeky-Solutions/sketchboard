package com.example.lostnfound.service.post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;

@Service
@Component
public class PostServiceiImpl implements PostService {
    @Autowired
    private PostRepo postRepo;

    @Override
    public Post savePost(Post post) {
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
            if(Objects.nonNull(postToUpdate.getTime()) && !"".equalsIgnoreCase(postToUpdate.getTime())) {
                postToUpdate.setTime(post.getTime());
            }
            if(Objects.nonNull(postToUpdate.getCategory()) && !"".equalsIgnoreCase(postToUpdate.getCategory())) {
                postToUpdate.setCategory(post.getCategory());
            }
            if(Objects.nonNull(postToUpdate.getStatus()) && !"".equalsIgnoreCase(postToUpdate.getStatus())) {
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

}
