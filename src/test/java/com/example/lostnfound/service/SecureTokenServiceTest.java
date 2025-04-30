package com.example.lostnfound.service;

import com.example.lostnfound.model.SecureToken;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.SecureTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecureTokenServiceTest {

    @Mock
    private SecureTokenRepo secureTokenRepo;

    @InjectMocks
    private SecureTokenService secureTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(secureTokenService, "tokenValidityInSecond", 3600); // mock @Value
    }

    @Test
    void testCreateToken_Success() {
        User user = new User();
        user.setUserId(1L);

        SecureToken token = secureTokenService.createToken(user);

        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertNotNull(token.getToken());
        assertNotNull(token.getExpiredAt());
        assertTrue(token.getExpiredAt().isAfter(LocalDateTime.now()));

        verify(secureTokenRepo, times(1)).save(any(SecureToken.class));
    }

    @Test
    void testSaveSecureToken_WithValidUser() {
        User user = new User();
        user.setUserId(1L);
        SecureToken token = new SecureToken();
        token.setUser(user);

        secureTokenService.saveSecureToken(token);

        verify(secureTokenRepo).save(token);
    }

    @Test
    void testSaveSecureToken_WithNullUser_ShouldThrowException() {
        SecureToken token = new SecureToken();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                secureTokenService.saveSecureToken(token));

        assertEquals("User cannot be null", exception.getMessage());
        verify(secureTokenRepo, never()).save(any());
    }

    @Test
    void testFindByToken() {
        String tokenStr = "sampleToken123";
        SecureToken expectedToken = new SecureToken();
        expectedToken.setToken(tokenStr);

        when(secureTokenRepo.findByToken(tokenStr)).thenReturn(expectedToken);

        SecureToken result = secureTokenService.findByToken(tokenStr);
        assertNotNull(result);
        assertEquals(tokenStr, result.getToken());
        verify(secureTokenRepo).findByToken(tokenStr);
    }

    @Test
    void testRemoveToken() {
        SecureToken token = new SecureToken();
        token.setToken("toRemove");

        secureTokenService.removeToken(token);
        verify(secureTokenRepo).delete(token);
    }
}
