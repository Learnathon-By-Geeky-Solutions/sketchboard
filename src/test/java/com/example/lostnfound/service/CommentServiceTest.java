package com.example.lostnfound.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import com.example.lostnfound.dto.CommentDto;
import com.example.lostnfound.model.Comment;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.CommentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepo commentRepo;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUserId(10L);

        Post post = new Post();
        post.setId(20L);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSaveComment() {
        when(commentRepo.save(any(Comment.class))).thenReturn(comment);

        Comment saved = commentService.saveComment(comment);

        assertNotNull(saved);
        assertEquals("Test comment", saved.getContent());
        verify(commentRepo).save(comment);
    }

    @Test
    void testGetAllCommentDtos() {
        when(commentRepo.findAll()).thenReturn(List.of(comment));

        List<CommentDto> dtos = commentService.getAllCommentDtos();

        assertEquals(1, dtos.size());
        assertEquals(comment.getId(), dtos.get(0).getId());
        assertEquals(comment.getContent(), dtos.get(0).getContent());
    }

    @Test
    void testGetCommentDtoById_Found() {
        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));

        CommentDto dto = commentService.getCommentDtoById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getContent());
    }

    @Test
    void testGetCommentDtoById_NotFound() {
        when(commentRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.getCommentDtoById(1L));
    }

    @Test
    void testDeleteComment() {
        doNothing().when(commentRepo).deleteById(1L);

        commentService.deleteComment(1L);

        verify(commentRepo).deleteById(1L);
    }

    @Test
    void testUpdateComment_Found() {
        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepo.save(any(Comment.class))).thenReturn(comment);

        Comment updated = commentService.updateComment(1L, "Updated");

        assertNotNull(updated);
        assertEquals("Updated", updated.getContent());
    }

    @Test
    void testUpdateComment_NotFound() {
        when(commentRepo.findById(1L)).thenReturn(Optional.empty());

        Comment result = commentService.updateComment(1L, "New content");

        assertNull(result);
    }
}
