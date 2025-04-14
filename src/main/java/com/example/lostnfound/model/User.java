package com.example.lostnfound.model;

import com.example.lostnfound.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @Column(name = "sumofweights")
    private float sumOfWeights;

    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 512) // dimensions
    private float[] embedding;

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

    // for Email Verification
    private boolean accountVerified = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<SecureToken> token;

    //my msg list
    @OneToMany(mappedBy = "senderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> receivedMessages;

    public void addInteraction(float[] embedding, float weight){
        /*
            The formula for updating the embedding is as follows:
            new_embedding = (embedding * weight) + (old_embedding * sumOfWeights) / (sumOfWeights + weight)
         */
        float[] mulofnew = new float[embedding.length];
        for(int i = 0; i < embedding.length; i++){
            mulofnew[i] = (embedding[i] * weight);
        }

        float[] mulofOld = new float[this.embedding.length];
        for(int i = 0; i < embedding.length; i++){
            mulofOld[i] = (this.embedding[i] * this.sumOfWeights);
        }

        this.embedding = mulofnew;
        this.sumOfWeights += weight;
        for(int i = 0; i < embedding.length; i++){
            this.embedding[i] = (mulofOld[i] + mulofnew[i]) / this.sumOfWeights;
        }
    }

}