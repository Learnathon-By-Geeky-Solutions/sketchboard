package com.example.lostnfound.dto;

import com.example.lostnfound.enums.Category;
import com.example.lostnfound.enums.Status;
import lombok.Data;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class PostDto {
    int id;
    String title;
    String description;
    String location;
    LocalDate date;
    LocalTime time;
    Category category;
    Status status;
    int range;
    LocalDateTime uploadTime;
    LocalDateTime lastUpdatedTime;
    long userId;
    private List<Long> commentIds;
}

