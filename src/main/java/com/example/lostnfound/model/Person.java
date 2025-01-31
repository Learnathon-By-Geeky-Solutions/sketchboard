package com.example.lostnfound.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Person")
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Persion_id;
    @Column(name = "Name")
    private String name;
    @Column(name = "Email", unique = true)
    private String email;
    @Column(name = "Password")
    private String password;
    @Column(name = "Address")
    private String address;
    @Column(name = "Dept")
    private String dept;

    @OneToMany (cascade = CascadeType.ALL)
    @JoinColumn (name = "person_id", referencedColumnName = "Persion_id")
    private List<Post> posts;

}
