package com.example.lostnfound.service.Comment;

import com.example.lostnfound.model.Comment;
import com.example.lostnfound.repository.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepo commentRepo;

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepo.save(comment);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepo.findAll();
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepo.deleteById(id);
    }

    @Override
    public Comment updateComment(Long id, Comment comment) {
        Optional<Comment> existingComment = commentRepo.findById(id);
        if (existingComment.isPresent()) {
            Comment updatedComment = existingComment.get();
            updatedComment.setContent(comment.getContent());
            // Update other fields as necessary
            return commentRepo.save(updatedComment);
        } else {
            return null;
        }
    }
}