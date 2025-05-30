package com.example.lostnfound.controller;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UnknownIdentifierException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.service.user.UserAccountService;
import com.example.lostnfound.service.user.UserService;
import com.example.lostnfound.util.CodeUtils;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PasswordController {

    private final UserAccountService userAccountService;
    private final UserService userService;

    public PasswordController(UserAccountService userAccountService, UserService userService) {
        this.userAccountService = userAccountService;
	    this.userService = userService;
    }

    @PostMapping("/forgotPassword")
    @Operation(summary = "Forgot Password", description = "Send a reset password email to the user")
    public ResponseEntity<Object> forgotPassword(@RequestParam String email) {
        if (email == null || email.isBlank() ||  !CodeUtils.EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format");
        }
        
        try {
            if(userService.findByEmail(email) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found with this email");
            }
            userAccountService.forgotPassword(email);
            return ResponseEntity.status(HttpStatus.OK).body("Reset email sent to " + email);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset the password using the token")
    public ResponseEntity<Object> updatePassword(@RequestParam String token, @RequestParam String password) throws UnknownIdentifierException {
        try {
            userAccountService.updatePassword(password, token);
            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
