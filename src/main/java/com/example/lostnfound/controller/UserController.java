package com.example.lostnfound.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.lostnfound.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import com.example.lostnfound.dto.UserDto;
import com.example.lostnfound.exception.UserNotAuthenticatedException;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.model.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, PasswordEncoder encoder, ModelMapper modelMapper) {
        this.userService = userService;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user")
    public ResponseEntity<UserDto> register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEmbedding(new float[3072]);
        user.setMessages(List.of());
        User registeredUser = userService.save(user);
        if (registeredUser != null) {
            UserDto userDto = modelMapper.map(registeredUser, UserDto.class);
            return new ResponseEntity<>(userDto, HttpStatus.CREATED);
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
        if (!Objects.equals(token, "Login Failed")) {
            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unauthorized access. Please check your credentials.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Get user profile by id", description = "Retrieves user's profile by id")
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getUser(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        
        if (user != null) {
            List<Post> posts = userService.findPostsByUserId(user.getUserId());
            System.out.println("User embed " + Arrays.toString(user.getEmbedding()));
            UserProfileResponse response = new UserProfileResponse(modelMapper.map(user, UserDto.class), posts);
            return new ResponseEntity<>(response, HttpStatus.OK);
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
            System.out.println("User embed " + Arrays.toString(user.getEmbedding()));
            UserProfileResponse response = new UserProfileResponse(modelMapper.map(user, UserDto.class), posts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new UserNotAuthenticatedException( "User not authenticated");
    }

    @Operation(summary = "Validate JWT token", description = "Validates JWT token")
    @GetMapping("/validate")
    public ResponseEntity<String> validate() {
        return new ResponseEntity<>("Token is valid", HttpStatus.OK);
    }

    @GetMapping("/getMyMessages")
    @Operation(summary = "Get messages", description = "Retrieves messages for user")
    public ResponseEntity<List<Long>> getMyMessages() {
        User user = userService.getCurrentUser();
        return new ResponseEntity<>(user.getMessages(), HttpStatus.OK);
    }

    @GetMapping("/myId")
    @Operation(summary = "Get user id", description = "Retrieves user id")
    public ResponseEntity<Long> getMyId() {
        User user = userService.getCurrentUser();
        return new ResponseEntity<>(user.getUserId(), HttpStatus.OK);
    }
}
