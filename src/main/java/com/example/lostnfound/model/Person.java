package com.example.lostnfound.model;

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
}
