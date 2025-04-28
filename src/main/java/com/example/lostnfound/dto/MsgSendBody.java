package com.example.lostnfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MsgSendBody {
    @Schema(description = "Id of the Message.", example = "1")
    private Long id;

    @NotEmpty
    @Schema(description = "Sender Id of the Message.", example = "1", required = true)
    private Long receiverId;

    @NotNull
    @Schema(description = "Content Id of the Message.", example = "is this available", required = true)
    String content;
}
