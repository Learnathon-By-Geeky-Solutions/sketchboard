package com.example.lostnfound.dto;

import lombok.Data;

@Data
public class msgSendBody {
    private Long receiverId;
    String content;
}
