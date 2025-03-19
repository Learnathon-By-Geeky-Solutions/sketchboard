package com.example.lostnfound.controller;

import com.example.lostnfound.dto.CommentDto;
import com.example.lostnfound.model.Comment;
import com.example.lostnfound.service.CommentService;
import com.example.lostnfound.service.PostService;
import com.example.lostnfound.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Tag(name = "Comment APIs", description = "REST APIs related to comments for CRUD operations.Like create, read, update, delete and search comments.")
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
    @Operation(summary = "Create Comment", description = "Create comment using post id and its content")
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto comment) {
        Comment newComment = new Comment();
        newComment.setContent(comment.getContent());
        newComment.setPost(postService.getPost(comment.getPostId()));
        newComment.setUser(userService.getCurrentUser());
        Comment savedComment = commentService.saveComment(newComment);
        CommentDto commentDto = new CommentDto(savedComment.getId(), savedComment.getContent(), savedComment.getUser().getUserId(), savedComment.getPost().getId(), savedComment.getCreatedAt());
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get All Comments", description = "Get all comments of a post")
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> comments = commentService.getAllCommentDtos();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Comment", description = "Get details of a comment by its id")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentDtoById(id);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Comment", description = "Update comment by its id")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") Long id, @RequestBody CommentDto commentDto) {
        Comment updatedComment = commentService.updateComment(id, commentDto.getContent());
        CommentDto newcommentDto = new CommentDto(updatedComment.getId(), updatedComment.getContent(), updatedComment.getUser().getUserId(), updatedComment.getPost().getId(), updatedComment.getCreatedAt());
        return new ResponseEntity<>(newcommentDto, HttpStatus.OK);
    }
    

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Comment", description = "Delete comment by its id")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}