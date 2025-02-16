package com.example.lostnfound.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    // @ManyToOne
    // @JoinColumn(name = "post_id", nullable = false)
    // private Post post;

    // @ManyToOne
    // @JoinColumn(name = "user_id", nullable = false)
    // private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}