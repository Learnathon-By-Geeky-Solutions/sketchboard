package com.example.lostnfound.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.lostnfound.enums.Catagory;
import com.example.lostnfound.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Getter
@Setter
@Entity
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title",nullable = false, columnDefinition = "varchar(255)") 
    private String title;

    @Column(name = "description",columnDefinition = "varchar(255) default 'Unknown'")
    private String description;

    @Column(name = "location",nullable = false, columnDefinition = "varchar(255)")
    private String location;

    @Column(name = "date",nullable = false, columnDefinition = "DATE")
    private LocalDate date;

    @Column(name = "time", nullable = false, columnDefinition = "TIME")
    private LocalTime time;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Catagory category;

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

    @PrePersist
    protected void onCreate() {
        this.uploadTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedTime = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
