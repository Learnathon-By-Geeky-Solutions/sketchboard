package com.example.lostnfound.service;

import com.example.lostnfound.model.SecureToken;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.SecureTokenRepo;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SecureTokenService {

    private static final BytesKeyGenerator bytesKeyGenerator = KeyGenerators.secureRandom(12);

    @Value("${app.token.validity}")
    private int tokenValidityInSecond;
    private final SecureTokenRepo secureTokenRepo;

    public SecureTokenService(SecureTokenRepo secureTokenRepo) {
        this.secureTokenRepo = secureTokenRepo;
    }

    public SecureToken createToken(User user) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(bytesKeyGenerator.generateKey()));
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(tokenValue);
        secureToken.setExpiredAt(LocalDateTime.now().plusSeconds(tokenValidityInSecond));
        secureToken.setUser(user);
        this.saveSecureToken(secureToken);
        return secureToken;
    }

    public void saveSecureToken(SecureToken token) {
        if (token.getUser() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        secureTokenRepo.save(token);
    }

    public SecureToken findByToken(String token) {
        return secureTokenRepo.findByToken(token);
    }

    public void removeToken(SecureToken token) {
        secureTokenRepo.delete(token);
    }
}