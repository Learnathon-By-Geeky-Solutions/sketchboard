package com.example.lostnfound.dto;

import lombok.Data;

@Data
public class PostDto {
    String title;
    String description;
    String location;
    String category;
    String status;
    String createdAt;
    String updatedAt;
    String user;
}

