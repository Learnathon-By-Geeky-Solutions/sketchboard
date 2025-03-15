package com.example.lostnfound.service.Comment;

import com.example.lostnfound.dto.CommentDto;
import com.example.lostnfound.model.Comment;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);
    List<CommentDto> getAllCommentDtos();
    CommentDto getCommentDtoById(Long id);
    void deleteComment(Long id);
    Comment updateComment(Long id, Comment comment);
}