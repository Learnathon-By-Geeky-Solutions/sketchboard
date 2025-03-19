package com.example.lostnfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class aiSearchQuery {
    @NotEmpty
    @Schema(description = "Query for AI Search.", example = "I lost my phone in the library", required = true)
    String query;

    @Schema(description = "Limit of the AI Search.", example = "10")
    Long limit;

    @Schema(description = "Post Id of the AI Search.", example = "1")
    Long postId;
}
