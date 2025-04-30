package com.example.lostnfound.service.user;

import com.example.lostnfound.dto.UserDto;
import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UserAlreadyExistsException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.mailing.AccountVerificationEmailContext;
import com.example.lostnfound.model.SecureToken;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.UserRepo;
import com.example.lostnfound.service.EmailService;
import com.example.lostnfound.service.SecureTokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private SecureTokenService secureTokenService;
    @Mock private EmailService emailService;
    @Mock private Authentication authentication;
    @Mock private UserDetails userDetails;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    private User user;
    private SecureToken token;

    @BeforeEach
    void setUp() {
        // Inject a non-null baseUrl into userService
        ReflectionTestUtils.setField(userService, "baseUrl", "http://lostnfoundbd.duckdns.org:8080");

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setAccountVerified(false);

        token = new SecureToken();
        token.setToken("valid-token");
        token.setUser(user);
        token.setExpiredAt(java.time.LocalDateTime.of(2099, 12, 31, 0, 0));
    }

    private void setupSecurityContextWithUser(String email) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
    }

    @Test
    void testRegister_NewUser_Success() throws MessagingException {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);
        when(secureTokenService.createToken(user)).thenReturn(token);

        assertDoesNotThrow(() -> userService.register(user));

        verify(userRepo).save(user);
        verify(secureTokenService).createToken(user);
        verify(secureTokenService).saveSecureToken(token);
        verify(emailService).sendEmail(any(AccountVerificationEmailContext.class));
    }

    @Test
    void testRegister_ExistingUser_ThrowsException() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.register(user));
    }

    @Test
    void testFindByEmail_UserFound() throws UserNotFoundException {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        assertEquals(user, userService.findByEmail(user.getEmail()));
    }

    @Test
    void testFindByEmail_UserNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(user.getEmail()));
    }


    @Test
    void testVerifyUser_Success() throws InvalidTokenException, UserNotFoundException {
        when(secureTokenService.findByToken("valid-token")).thenReturn(token);
        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));

        userService.verifyUser("valid-token");

        assertTrue(user.isAccountVerified());
        verify(userRepo).save(user);
        verify(secureTokenService).removeToken(token);
    }

    @Test
    void testVerifyUser_InvalidToken_ThrowsException() {
        SecureToken expiredToken = new SecureToken();
        expiredToken.setToken("invalid-token");
        expiredToken.setUser(user);
        expiredToken.setExpiredAt(Timestamp.valueOf("2000-01-01 00:00:00").toLocalDateTime());
        when(secureTokenService.findByToken("invalid-token")).thenReturn(expiredToken);
        assertThrows(InvalidTokenException.class, () -> userService.verifyUser("invalid-token"));
    }

    @Test
    void testUpdatePassword() {
        userService.updatePassword(user);
        verify(userRepo).save(user);
    }

    @Test
    void testCheckIfUserExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        assertTrue(userService.checkIfUserExist(user.getEmail()));
    }

    @Test
    void testGetCurrentUser() throws UserNotFoundException {
        setupSecurityContextWithUser(user.getEmail());
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);

        User result = userService.getCurrentUser();
        assertEquals(user, result);
    }

    @Test
    void testUpdateUser_Success() throws UserNotFoundException {
        UserDto dto = new UserDto();
        dto.setName("New Name");
        dto.setEmail("new@example.com");
        dto.setDepartment("Dept");
        dto.setAddress("Address");
        setupSecurityContextWithUser(user.getEmail());

        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);

        userService.update(dto);

        verify(userRepo).save(user);
        assertEquals("New Name", user.getName());
    }
}
