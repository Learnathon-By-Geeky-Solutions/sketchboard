package com.example.lostnfound.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1024) // dimensions
    private float[] embedding;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    private Long fileSize;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @OneToOne(mappedBy = "image")
    @JsonIgnore
    private Post post;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}