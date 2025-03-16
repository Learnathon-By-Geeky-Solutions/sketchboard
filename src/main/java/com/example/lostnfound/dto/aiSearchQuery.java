package com.example.lostnfound.dto;

import lombok.Data;

@Data
public class aiSearchQuery {
    String query;
    Long limit;
    Long postId;
}
