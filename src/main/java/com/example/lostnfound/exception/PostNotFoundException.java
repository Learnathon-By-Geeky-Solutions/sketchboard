package com.example.lostnfound.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends Exception {
    public PostNotFoundException(String message) {
        super(message);
    }

}
