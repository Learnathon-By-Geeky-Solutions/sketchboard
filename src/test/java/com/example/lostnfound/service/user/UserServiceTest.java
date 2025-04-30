package com.example.lostnfound.service.user;

import com.example.lostnfound.exception.EmailSendException;
import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.PostNotFoundException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.mailing.AccountVerificationEmailContext;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.SecureToken;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.PostRepo;
import com.example.lostnfound.repository.UserRepo;
import com.example.lostnfound.service.EmailService;
import com.example.lostnfound.service.SecureTokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private PostRepo postRepo;
    @Mock private SecureTokenService secureTokenService;
    @Mock private EmailService emailService;
    @Mock private AuthenticationManager authmManager;
    @Mock private JWTService jwtService;
    @Mock private Authentication authentication;
    @Mock private UserDetails userDetails;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    private User user;

    private SecureToken validToken;
    private SecureToken expiredToken;
    private Post post;

    @BeforeEach
    void setUp() {
        // inject baseUrl for email context
        ReflectionTestUtils.setField(userService, "baseUrl", "http://localhost");


        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setAccountVerified(false);


        // valid token
        validToken = new SecureToken();
        validToken.setToken("valid-token");
        validToken.setUser(user);
        validToken.setExpiredAt(java.time.LocalDateTime.of(2099, 12, 31, 0, 0));

        // expired token
        expiredToken = new SecureToken();
        expiredToken.setToken("expired-token");
        expiredToken.setUser(user);
        expiredToken.setExpiredAt(java.time.LocalDateTime.of(2000, 1, 1, 0, 0));

        post = new Post();
        post.setId(10L);
    }

    private void setupSecurityContextWithUser(String email) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
    }

    @Test
    void testSaveUser() {
        userService.save(user);
        verify(userRepo).save(user);
    }

    @Test
    void testVerify_Success() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        Authentication authMock = mock(Authentication.class);
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authmManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        when(jwtService.generateToken(user.getEmail())).thenReturn("jwt-token");

        String result = userService.verify(user.getEmail(), "password");
        assertEquals("jwt-token", result);
        verify(authmManager).authenticate(any());
        verify(jwtService).generateToken(user.getEmail());
    }

    @Test
    void testVerify_UserNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);
        String result = userService.verify(user.getEmail(), "password");
        assertEquals("Login Failed", result);
        verify(authmManager, never()).authenticate(any());
    }

    @Test
    void testVerify_AuthenticationFailed() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        Authentication authMock = mock(Authentication.class);
        when(authMock.isAuthenticated()).thenReturn(false);
        when(authmManager.authenticate(any())).thenReturn(authMock);

        String result = userService.verify(user.getEmail(), "badpwd");
        assertEquals("Login Failed", result);
        verify(jwtService, never()).generateToken(any());
    }
    @Test
    void testFindPostsByUserId_Found() throws PostNotFoundException {
        when(postRepo.findByUserId(user.getUserId())).thenReturn(List.of(post));
        List<Post> posts = userService.findPostsByUserId(user.getUserId());
        assertEquals(1, posts.size());
    }

    @Test
    void testFindPostsByUserId_NotFound() {
        when(postRepo.findByUserId(user.getUserId())).thenReturn(null);
        assertThrows(PostNotFoundException.class,
                () -> userService.findPostsByUserId(user.getUserId()));
    }

    @Test
    void testFindById_Success() throws UserNotFoundException {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        User u = userService.findById(1L);
        assertEquals(user, u);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    // Context1: checkIfUserExist
    @Test
    void testCheckIfUserExist_Exists() {

        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        assertTrue(userService.checkIfUserExist(user.getEmail()));
    }

    @Test
    void testCheckIfUserExist_NotExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);
        assertFalse(userService.checkIfUserExist(user.getEmail()));
    }

    // Context2: getCurrentUser branches
    @Test
    void testGetCurrentUser_Success() throws UserNotFoundException {
        setupSecurityContextWithUser(user.getEmail());
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        User current = userService.getCurrentUser();
        assertEquals(user, current);
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        setupSecurityContextWithUser(user.getEmail());
        when(userRepo.findByEmail(user.getEmail())).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
    }

    // Context3: sendRegistrationEmail catch
    @Test
    void testSendRegistrationEmail_Success() throws MessagingException {
        when(secureTokenService.createToken(user)).thenReturn(validToken);
        doNothing().when(emailService).sendEmail(any(AccountVerificationEmailContext.class));
        assertDoesNotThrow(() -> userService.sendRegistrationEmail(user));
        verify(emailService).sendEmail(any());
    }

    @Test
    void testSendRegistrationEmail_Failure() throws MessagingException {
        when(secureTokenService.createToken(user)).thenReturn(validToken);
        doThrow(MessagingException.class).when(emailService).sendEmail(any());
        assertThrows(EmailSendException.class,
                () -> userService.sendRegistrationEmail(user));
    }

    // Context4: verifyUser branches
    @Test
    void testVerifyUser_Success() throws InvalidTokenException, UserNotFoundException {
        when(secureTokenService.findByToken("valid-token")).thenReturn(validToken);
        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        userService.verifyUser("valid-token");
        assertTrue(user.isAccountVerified());
        verify(userRepo).save(user);
        verify(secureTokenService).removeToken(validToken);
    }

    @Test
    void testVerifyUser_NullToken() {
        assertThrows(InvalidTokenException.class,
                () -> userService.verifyUser(null));
    }

    @Test
    void testVerifyUser_MismatchedToken() {
        when(secureTokenService.findByToken("some-token")).thenReturn(validToken);
        assertThrows(InvalidTokenException.class,
                () -> userService.verifyUser("some-token"));
    }

    @Test
    void testVerifyUser_ExpiredToken() {
        when(secureTokenService.findByToken("expired-token")).thenReturn(expiredToken);
        assertThrows(InvalidTokenException.class,
                () -> userService.verifyUser("expired-token"));
    }

    @Test
    void testVerifyUser_UserNotFound() {
        when(secureTokenService.findByToken("valid-token")).thenReturn(validToken);
        when(userRepo.findById(user.getUserId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> userService.verifyUser("valid-token"));
    }

    // Context1: verify(email,password) also throws on invalid password
    @Test
    void testVerify_EmailPasswordNull() {
        String result = userService.verify(null, null);
        assertEquals("Login Failed", result);

    }
}
