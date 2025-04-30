package com.example.lostnfound.service.user;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    private JWTService jwtService;

    private static final String BASE64_SECRET =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService(BASE64_SECRET);
    }

    @Test
    void generateToken_and_validateToken_forSameUser() {
        String email = "user@example.com";

        String token = jwtService.generateToken(email);
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
        when(userDetails.getUsername()).thenReturn(email);
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void extractEmail_returnsCorrectSubject() {
        String email = "another@example.com";
        String token = jwtService.generateToken(email);

        String extracted = jwtService.extractEmail(token);
        assertEquals(email, extracted);
    }


    @Test
    void extractEmail_throwsOnInvalidToken() {
        String invalid = "abc.def.ghi";
        assertThrows(JwtException.class, () -> jwtService.extractEmail(invalid));
    }
}
