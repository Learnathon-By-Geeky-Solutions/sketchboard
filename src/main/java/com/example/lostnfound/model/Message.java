package com.example.lostnfound.model;

import com.example.lostnfound.enums.MessageReadStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "senderId", nullable = false)
    private Long senderId;

    @Column(name = "receiverId", nullable = false)
    private Long receiverId;

    @Column(name = "read_status", nullable = false)
    private MessageReadStatus readStatus;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
