package com.example.lostnfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class msgSendBody {
    @NotEmpty
    @Schema(description = "Sender Id of the Message.", example = "1", required = true)
    private Long receiverId;

    @NotEmpty
    @Schema(description = "Receiver Id of the Message.", example = "is this available", required = true)
    String content;
}
