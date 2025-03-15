package com.example.lostnfound.service.Comment;

import com.example.lostnfound.dto.CommentDto;
import com.example.lostnfound.model.Comment;
import com.example.lostnfound.repository.CommentRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepo;
    public CommentServiceImpl(CommentRepo commentRepo) {
        this.commentRepo = commentRepo;
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepo.save(comment);
    }

    @Override
    public List<CommentDto> getAllCommentDtos() {
        return commentRepo.findAll().stream()
                .map(comment -> new CommentDto(comment.getContent(), comment.getUser().getUserId(), comment.getPost().getId() , comment.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentDtoById(Long id) {
        Optional<Comment> comment = Optional.ofNullable(commentRepo.findById(id).orElse(null));
        return new CommentDto(comment.get().getContent(), comment.get().getUser().getUserId(), comment.get().getPost().getId(), comment.get().getCreatedAt());
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