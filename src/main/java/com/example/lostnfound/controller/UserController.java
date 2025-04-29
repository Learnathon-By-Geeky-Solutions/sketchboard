package com.example.lostnfound.controller;

import java.util.List;
import java.util.Objects;
import com.example.lostnfound.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.lostnfound.dto.LoginDto;
import com.example.lostnfound.dto.PasswordDto;
import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.dto.UserDto;
import com.example.lostnfound.exception.UserNotAuthenticatedException;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.model.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
    public ResponseEntity<Object> register(@RequestBody UserDto user) {
        try {
            User newUser = new User();
            newUser.setEmail(user.getEmail());
            newUser.setPassword(encoder.encode(user.getPassword()));
            newUser.setName(user.getName());
            newUser.setDepartment(user.getDepartment());
            newUser.setAddress(user.getAddress());
            newUser.setRole(user.getRole());
            newUser.setEmbedding(new float[1024]);
            userService.register(newUser);
            return new ResponseEntity<>(modelMapper.map(newUser, UserDto.class), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Logs in a user")
    public ResponseEntity<Object> login(@RequestBody LoginDto user) {
        try {
            String mail = user.getEmail();
            String password = user.getPassword();
            String token = userService.verify(mail, password);
            if (!Objects.equals(token, "Login Failed")) {
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Unauthorized access. Please check your credentials.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Get user profile by id", description = "Retrieves user's profile by id")
    @GetMapping("/profile/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                return getUserProfileResponseResponseEntity(user);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private ResponseEntity<Object> getUserProfileResponseResponseEntity(User user) {
        try {
            List<Post> posts = userService.findPostsByUserId(user.getUserId());
            List<PostDto> postDtos = posts.stream()
                    .map(post -> modelMapper.map(post, PostDto.class))
                    .toList();
            UserProfileResponse response = new UserProfileResponse(modelMapper.map(user, UserDto.class), postDtos);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Get user profile", description = "Retrieves authenticated user's profile and posts")
    @GetMapping("/profile")
    public ResponseEntity<Object> profileUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                User user = userService.findByEmail(email);
                if (user == null) {
                    throw new UserNotAuthenticatedException("User not found");
                }
                return getUserProfileResponseResponseEntity(user);
            }
            return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/updateProfile")
    @Operation(summary = "Update user profile", description = "Updates user profile")
    public ResponseEntity<Object> updateProfile(@RequestBody UserDto user) {
        try {

            userService.update(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Validate JWT token", description = "Validates JWT token")
    @GetMapping("/validate")
    public ResponseEntity<Object> validate() {
        try{
            return new ResponseEntity<>("Token is valid", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/myId")
    @Operation(summary = "Get user id", description = "Retrieves user id")
    public ResponseEntity<Object> getMyId() {
        try {
            User user = userService.getCurrentUser();
            return new ResponseEntity<>(user.getUserId(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

        @PutMapping("/profile/password")
    @Operation(summary = "Change password", description = "Changes password for user")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordDto user) {
           try {
            User currentUser = userService.getCurrentUser();
            if (encoder.matches(user.getCurrentPassword(), currentUser.getPassword())) {
                currentUser.setPassword(encoder.encode(user.getNewPassword()));
                userService.updatePassword(currentUser);
                return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
            } else {

                return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
            }
      } catch (Exception e) {
        
          return new ResponseEntity<>("Error processing password change", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
}
