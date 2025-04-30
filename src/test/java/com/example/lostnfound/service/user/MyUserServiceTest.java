package com.example.lostnfound.service.user;


import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private MyUserService myUserService;

    private User user;

    @BeforeEach
    void setUp() {
        // Create a sample user
        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setUserId(1L);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        UserDetails userDetails = myUserService.loadUserByUsername(user.getEmail());
        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepo.findByEmail(email)).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> myUserService.loadUserByUsername(email));
    }
}
