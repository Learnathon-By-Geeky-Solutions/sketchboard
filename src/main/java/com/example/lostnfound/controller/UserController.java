package com.example.lostnfound.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RestController;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.model.UserProfileResponse;
import com.example.lostnfound.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.lostnfound.exception.UserNotAuthenticatedException;

@RestController
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    public UserController(UserService userService, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userService.userRegister(user);
    }
    
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> userMap) {
        String mail=userMap.get("email");
        String password=userMap.get("password");
        return userService.verify(mail, password);
    }

    @GetMapping("/profile")
    public UserProfileResponse profileUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }
        if (email != null) {
            User user = userService.findByEmail(email);
            List<Post> posts = userService.findPostsByUserId(user.getUserId());
            return new UserProfileResponse(user, posts);
        } else {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
    }
    
    
}
