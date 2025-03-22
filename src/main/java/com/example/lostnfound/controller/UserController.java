package com.example.lostnfound.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.lostnfound.model.Message;
import com.example.lostnfound.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.lostnfound.dto.LoginDto;
import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.dto.UserDto;
import com.example.lostnfound.exception.UserNotAuthenticatedException;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.model.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


@RestController
@Tag(name = "User APIs", description = "REST APIs related to user for CRUD operations.Like create, read, update, delete and search user.")
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
    public ResponseEntity<?> register(@RequestBody UserDto user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encoder.encode(user.getPassword()));
        newUser.setName(user.getName());
        newUser.setDepartment(user.getDepartment());
        newUser.setAddress(user.getAddress());
        newUser.setRole(user.getRole());
        newUser.setEmbedding(new float[3072]);
        System.out.println("New created user is: " + newUser);
        try{
            userService.register(newUser);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(modelMapper.map(newUser, UserDto.class), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Logs in a user")
    public ResponseEntity<String> login(@RequestBody LoginDto user) {
        String mail = user.getEmail();
        String password = user.getPassword();
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
            return getUserProfileResponseResponseEntity(user);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<UserProfileResponse> getUserProfileResponseResponseEntity(User user) {
        List<Post> posts = userService.findPostsByUserId(user.getUserId());
        List<PostDto> postDtos = posts.stream()
                                      .map(post -> modelMapper.map(post, PostDto.class))
                                      .collect(Collectors.toList());
        System.out.println("User embed " + Arrays.toString(user.getEmbedding()));
        UserProfileResponse response = new UserProfileResponse(modelMapper.map(user, UserDto.class), postDtos);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
            return getUserProfileResponseResponseEntity(user);
        }
        throw new UserNotAuthenticatedException( "User not authenticated");
    }

    @PutMapping("/updateProfile")
    @Operation(summary = "Update user profile", description = "Updates user profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto user) {
        User updatedUser = new User();
        updatedUser.setEmail(user.getEmail());
        updatedUser.setName(user.getName());
        updatedUser.setDepartment(user.getDepartment());
        updatedUser.setAddress(user.getAddress());
        updatedUser.setRole(user.getRole());
        updatedUser.setEmbedding(new float[3072]);
        userService.update(updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Validate JWT token", description = "Validates JWT token")
    @GetMapping("/validate")
    public ResponseEntity<String> validate() {
        return new ResponseEntity<>("Token is valid", HttpStatus.OK);
    }


    @GetMapping("/myId")
    @Operation(summary = "Get user id", description = "Retrieves user id")
    public ResponseEntity<Long> getMyId() {
        User user = userService.getCurrentUser();
        return new ResponseEntity<>(user.getUserId(), HttpStatus.OK);
    }
}
