package com.example.lostnfound.service.Comment;

import com.example.lostnfound.model.Comment;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);
    List<Comment> getAllComments();
    Comment getCommentById(Long id);
    void deleteComment(Long id);
}