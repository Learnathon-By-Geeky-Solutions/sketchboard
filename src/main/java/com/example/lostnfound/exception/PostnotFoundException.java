package com.example.lostnfound.exception;
import org.springframework.http.HttpStatus;

public class PostnotFoundException extends RuntimeException {
    public PostnotFoundException(String message,HttpStatus status) {
        super(message);
    }

}
