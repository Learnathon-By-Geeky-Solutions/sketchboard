package com.example.lostnfound.model;



import java.security.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "location")
    private String location;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "time")
    private String time;
    @Column(name = "category")
    private String category;
    @Column(name = "status")
    private String status;
    @Column(name = "range")
    private int range;
    @Column(name = "uploadtime", insertable = false, updatable = false)
    private Timestamp uploadTime;
    @Column(name = "lastupdatedtime")
    private Timestamp lastUpdatedTime;

    
}
