package com.example.lostnfound.model;

import com.example.lostnfound.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "department", nullable = false)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}
