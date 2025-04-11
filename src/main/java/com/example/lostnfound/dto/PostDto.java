package com.example.lostnfound.dto;

import com.example.lostnfound.enums.Category;
import com.example.lostnfound.enums.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PostDto {
    @Schema(description = "Id of the Post.", example = "1")
    private Long id;

    @NotEmpty
    @Schema(description = "Title of the Post.", example = "Lost Phone", required = true)
    private String title;

    @NotEmpty
    @Schema(description = "Description of the Post.", example = "I lost my phone in the library", required = true)
    private String description;

    @NotEmpty
    @Schema(description = "Location of the Post.", example = "Library", required = true)
    private String location;

    @NotEmpty
    @Schema(description = "Date of the Post.", example = "2021-12-31", required = true)
    private LocalDate date;

    @NotEmpty
    @Schema(description = "Time of the Post.", example = "12:00", required = true)
    private LocalTime time;

    @NotEmpty
    @Schema(description = "Category of the Post.", example = "ELECTRONICS", required = true)
    private Category category;

    @NotEmpty
    @Schema(description = "Status of the Post.", example = "LOST", required = true)
    private Status status;

    @Min(1)
    @Schema(description = "Range of the Post.", example = "10", required = true)
    private int range;

    @Schema(description = "User Id of the Post.", example = "1")
    private Long userId;

    @Schema(description = "User Name of the Post.", example = "John Doe")
    private String userName;

    @Schema(description = "ID of the associated image", example = "1")
    private Long imageId;

}

