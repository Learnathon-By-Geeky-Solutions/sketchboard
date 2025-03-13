package com.example.lostnfound.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    int id;
    String title;
    String description;
    String location;
    String category;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String user;
}

