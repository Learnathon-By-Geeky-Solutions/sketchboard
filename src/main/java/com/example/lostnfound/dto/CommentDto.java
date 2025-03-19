package com.example.lostnfound.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @Schema(description = "Id of the Comment.", example = "1")
    private Long id;

    @NotEmpty
    @Schema(description = "Content of the Comment.", example = "I found your phone")
    private String content;

    @Schema(description = "User Id of the Comment.", example = "1")
    private Long userId;

    @Schema(description = "Post Id of the Comment.", example = "1")
    private  Long postId;

    @Schema(description = "Created At of the Comment.", example = "2021-12-31T12:00:00")
    private LocalDateTime createdAt;
}