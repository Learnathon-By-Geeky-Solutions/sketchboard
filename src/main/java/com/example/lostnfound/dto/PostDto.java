package com.example.lostnfound.dto;

import com.example.lostnfound.enums.Category;
import com.example.lostnfound.enums.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PostDto {
    @NotEmpty
    @Schema(description = "Title of the Post.", example = "Lost Phone", required = true)
    String title;

    @NotEmpty
    @Schema(description = "Description of the Post.", example = "I lost my phone in the library", required = true)
    String description;

    @NotEmpty
    @Schema(description = "Location of the Post.", example = "Library", required = true)
    String location;

    @NotEmpty
    @Schema(description = "Date of the Post.", example = "2021-12-31", required = true)
    LocalDate date;

    @NotEmpty
    @Schema(description = "Time of the Post.", example = "12:00", required = true)
    LocalTime time;

    @NotEmpty
    @Schema(description = "Category of the Post.", example = "ELECTRONICS", required = true)
    Category category;

    @NotEmpty
    @Schema(description = "Status of the Post.", example = "LOST", required = true)
    Status status;

    @NotEmpty
    @Schema(description = "Range of the Post.", example = "10", required = true)
    int range;


}

