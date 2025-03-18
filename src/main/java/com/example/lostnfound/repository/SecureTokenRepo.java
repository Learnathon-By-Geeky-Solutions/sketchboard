package com.example.lostnfound.repository;

import com.example.lostnfound.model.SecureToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureTokenRepo extends JpaRepository<SecureToken, Long> {

    SecureToken findByToken(String token);
    void removeToken(SecureToken token);
}
