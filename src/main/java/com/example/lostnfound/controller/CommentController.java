package com.example.lostnfound.controller;

import com.example.lostnfound.dto.CommentDto;
import com.example.lostnfound.model.Comment;
import com.example.lostnfound.service.Comment.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Comment createComment(@RequestBody @Valid Comment comment) {
        return commentService.saveComment(comment);
    }

    @GetMapping
    public List<CommentDto> getAllComments() {
        return commentService.getAllCommentDtos();
    }

    @GetMapping("/{id}")
    public CommentDto getCommentById(@PathVariable Long id) {
        return commentService.getCommentDtoById(id);
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        return commentService.updateComment(id, comment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}