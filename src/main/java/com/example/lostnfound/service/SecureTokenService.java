package com.example.lostnfound.service;

import com.example.lostnfound.model.SecureToken;

public interface SecureTokenService {

    SecureToken createToken();
    void saveSecureToken(SecureToken token);
    SecureToken findByToken(String token);
    void removeToken(SecureToken token);
}
