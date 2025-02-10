package com.example.lostnfound.model;

// import java.util.ArrayList;
// import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Person")
@NoArgsConstructor
@AllArgsConstructor

public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Address", nullable = false)
    private String address;

    @Column(name = "Dept", nullable = false)
    private String dept;

    // @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Post> posts = new ArrayList<>();
}
