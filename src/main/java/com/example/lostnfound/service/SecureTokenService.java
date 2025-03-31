package com.example.lostnfound.service;

import com.example.lostnfound.model.SecureToken;
import com.example.lostnfound.model.User;

public interface SecureTokenService {

    SecureToken createToken(User user);
    void saveSecureToken(SecureToken token);
    SecureToken findByToken(String token);
    void removeToken(SecureToken token);
}
