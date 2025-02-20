package com.example.lostnfound.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.example.lostnfound.dto.UserDto;
import com.example.lostnfound.exception.UserNotAuthenticatedException;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.model.UserProfileResponse;
import com.example.lostnfound.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    public UserController(UserService userService, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user")
    public ResponseEntity<User> register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        User registeredUser = userService.userRegister(user);
        if (registeredUser != null) {
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Logs in a user")
    public ResponseEntity<String> login(@RequestBody Map<String, String> userMap) {
        String mail = userMap.get("email");
        String password = userMap.get("password");
        String token = userService.verify(mail, password);
        if (token != null) {
            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Get user profile by id", description = "Retrieves user's profile by id")
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        UserDto userDto = userService.getUser(id);
        if (userDto != null) {
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @Operation(summary = "Get user profile", description = "Retrieves authenticated user's profile and posts")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> profileUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            User user = userService.findByEmail(email);
            if (user == null) {
                throw new UserNotAuthenticatedException( "User not found");
            }
            List<Post> posts = userService.findPostsByUserId(user.getUserId());
            UserProfileResponse response = new UserProfileResponse(user, posts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new UserNotAuthenticatedException( "User not authenticated");
    }
}
