package com.example.lostnfound.service.user;

import com.example.lostnfound.exception.EmailSendException;
import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UnknownIdentifierException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.mailing.ForgotPasswordEmailContext;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock private UserService userService;
    @Mock private SecureTokenService secureTokenService;
    @Mock private EmailService emailService;
    @Mock private UserRepo userRepo;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserAccountService userAccountService;

    private User user;
    private SecureToken token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setPassword("oldpassword");

        token = new SecureToken();
        token.setToken("valid-token");
        token.setUser(user);
        token.setExpiredAt(LocalDateTime.of(2099, 12, 31, 0, 0));
    }

    @Test
    void testForgotPassword_Success() throws UserNotFoundException, MessagingException {
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(secureTokenService.createToken(user)).thenReturn(token);

        userAccountService.forgotPassword("test@example.com");

        verify(emailService).sendEmail(any(ForgotPasswordEmailContext.class));
        verify(secureTokenService).saveSecureToken(token);
    }

    @Test
    void testForgotPassword_UserNotFound() throws UserNotFoundException {
        when(userService.findByEmail("missing@example.com")).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userAccountService.forgotPassword("missing@example.com"));
    }

    @Test
    void testUpdatePassword_Success() throws InvalidTokenException, UnknownIdentifierException {
        when(secureTokenService.findByToken("valid-token")).thenReturn(token);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode("newpassword")).thenReturn("hashedPassword");

        userAccountService.updatePassword("newpassword", "valid-token");

        verify(userRepo).save(user);
        verify(secureTokenService).removeToken(token);
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    void testUpdatePassword_TokenExpired() {
        token.setExpiredAt(LocalDateTime.of(2000, 1, 1, 0, 0)); // expired
        when(secureTokenService.findByToken("expired-token")).thenReturn(token);

        assertThrows(InvalidTokenException.class, () -> userAccountService.updatePassword("newpassword", "expired-token"));
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        when(secureTokenService.findByToken("valid-token")).thenReturn(token);
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UnknownIdentifierException.class, () -> userAccountService.updatePassword("newpassword", "valid-token"));
    }

    @Test
    void testUpdatePassword_NullToken() {
        when(secureTokenService.findByToken("null-token")).thenReturn(null);

        assertThrows(InvalidTokenException.class, () -> userAccountService.updatePassword("newpass", "null-token"));
    }

    @Test
    void testSendResetPasswordEmail_EmailSuccess() throws MessagingException {
        when(secureTokenService.createToken(user)).thenReturn(token);

        userAccountService.sendResetPasswordEmail(user);

        verify(emailService).sendEmail(any(ForgotPasswordEmailContext.class));
        verify(secureTokenService).saveSecureToken(token);
    }

    @Test
    void testSendResetPasswordEmail_EmailFailure() throws MessagingException {
        when(secureTokenService.createToken(user)).thenReturn(token);
        doThrow(MessagingException.class).when(emailService).sendEmail(any(ForgotPasswordEmailContext.class));

        assertThrows(EmailSendException.class, () -> userAccountService.sendResetPasswordEmail(user));
    }

    @Test
    void testSendResetPasswordEmail_TokenWithoutUser() throws MessagingException {
        SecureToken tokenWithoutUser = new SecureToken();
        tokenWithoutUser.setToken("token123");
        tokenWithoutUser.setExpiredAt(LocalDateTime.of(2099, 12, 31, 0, 0));
        tokenWithoutUser.setUser(null);

        when(secureTokenService.createToken(user)).thenReturn(tokenWithoutUser);

        userAccountService.sendResetPasswordEmail(user);

        verify(secureTokenService).saveSecureToken(tokenWithoutUser);
        verify(emailService).sendEmail(any(ForgotPasswordEmailContext.class));
    }
    @Test
    void testSendResetPasswordEmail_TokenWithoutUser_EmailFailure() throws MessagingException {
        SecureToken tokenWithoutUser = new SecureToken();
        tokenWithoutUser.setToken("token123");
        tokenWithoutUser.setExpiredAt(LocalDateTime.of(2099, 12, 31, 0, 0));
        tokenWithoutUser.setUser(null);

        when(secureTokenService.createToken(user)).thenReturn(tokenWithoutUser);
        doThrow(MessagingException.class).when(emailService).sendEmail(any(ForgotPasswordEmailContext.class));

        assertThrows(EmailSendException.class, () -> userAccountService.sendResetPasswordEmail(user));
        verify(secureTokenService).saveSecureToken(tokenWithoutUser);
    }
}
