package com.example.lostnfound.exception;
import org.springframework.http.HttpStatus;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String message,HttpStatus status) {
        super(message);
    }
}