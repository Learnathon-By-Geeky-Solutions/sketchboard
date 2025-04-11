package com.example.lostnfound.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.example.lostnfound.enums.Category;
import com.example.lostnfound.enums.Status;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Getter
@Setter
@Entity
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 3072) // dimensions
    private float[] embedding;

    @Column(name = "title",nullable = false, columnDefinition = "varchar(255)")
    private String title;

    @Column(name = "description",columnDefinition = "varchar(10000)")
    private String description;

    @Column(name = "location",nullable = false, columnDefinition = "varchar(255)")
    private String location;

    @Column(name = "date",nullable = false, columnDefinition = "DATE")
    private LocalDate date;

    @Column(name = "time", nullable = false, columnDefinition = "TIME")
    private LocalTime time;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "range",nullable = false, columnDefinition = "int default 0")
    private int range;
    
    @Column(name = "uploadtime")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime uploadTime;

    @Column(name = "lastupdatedtime")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastUpdatedTime;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @PrePersist
    protected void onCreate() {
        this.uploadTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedTime = LocalDateTime.now();
    }

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "image_path", columnDefinition = "varchar(255)")
    private String imagePath;

    public String infoForEmbedding(){
        String title5x = title + " " + title + " " + title + " " + title + " " + title;
        StringBuilder category10x = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            category10x.append(category).append(" ");
        }
        return title5x + " " + description + " " + title5x + " " + location + " " + category10x;
    }
}
