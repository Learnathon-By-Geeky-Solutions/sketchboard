package com.example.lostnfound.service;

import com.example.lostnfound.dto.CommentDto;
import com.example.lostnfound.model.Comment;
import com.example.lostnfound.repository.CommentRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepo commentRepo;
    public CommentService(CommentRepo commentRepo) {
        this.commentRepo = commentRepo;
    }

    public Comment saveComment(Comment comment) {
        return commentRepo.save(comment);
    }

    public List<CommentDto> getAllCommentDtos() {
        return commentRepo.findAll().stream()
                .map(comment -> new CommentDto(comment.getId(), comment.getContent(), comment.getUser().getUserId(), comment.getPost().getId() , comment.getCreatedAt()))
                .toList();
    }

    public CommentDto getCommentDtoById(Long id) {
        Optional<Comment> comment = commentRepo.findById(id);
        return comment.map(c -> new CommentDto(c.getId(), c.getContent(), c.getUser().getUserId(), c.getPost().getId(), c.getCreatedAt()))
                .orElseThrow(() -> new RuntimeException("Comment not found"));

    }

    public void deleteComment(Long id) {
        commentRepo.deleteById(id);
    }

    public Comment updateComment(Long id, String content) {
        Optional<Comment> existingComment = commentRepo.findById(id);
        if (existingComment.isPresent()) {
            Comment updatedComment = existingComment.get();
            updatedComment.setContent(content);
            // Update other fields as necessary
            return commentRepo.save(updatedComment);
        } else {
            return null;
        }
    }
}