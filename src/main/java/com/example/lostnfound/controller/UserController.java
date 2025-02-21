package com.example.lostnfound.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.example.lostnfound.model.User;
import com.example.lostnfound.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


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
    public ResponseEntity<String> login(@RequestBody Map<String, String> userMap) {
        String mail = userMap.get("email");
        String password = userMap.get("password");
        String result = userService.verify(mail, password);
        HttpStatus status = result.equals("Login Failed") ? HttpStatus.UNAUTHORIZED : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }
    
}
