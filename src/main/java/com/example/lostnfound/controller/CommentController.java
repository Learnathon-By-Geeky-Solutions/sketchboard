package com.example.lostnfound.controller;

import com.example.lostnfound.dto.CommentDto;
import com.example.lostnfound.model.Comment;
import com.example.lostnfound.service.CommentService;
import com.example.lostnfound.service.PostService;
import com.example.lostnfound.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    public CommentController(CommentService commentService, PostService postService, UserService userService) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto comment) {
        Comment newComment = new Comment();
        newComment.setContent(comment.getContent());
        newComment.setPost(postService.getPost(comment.getPostId()));
        newComment.setUser(userService.getCurrentUser());
        Comment savedComment = commentService.saveComment(newComment);
        CommentDto commentDto = new CommentDto(savedComment.getContent(), savedComment.getUser().getUserId(), savedComment.getPost().getId(), savedComment.getCreatedAt());
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> comments = commentService.getAllCommentDtos();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentDtoById(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(id, comment);
        CommentDto commentDto = new CommentDto(updatedComment.getContent(), updatedComment.getUser().getUserId(), updatedComment.getPost().getId(), updatedComment.getCreatedAt());
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}