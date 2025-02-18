package com.example.lostnfound.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GeminiInitializationException extends RuntimeException {
    public GeminiInitializationException(String message) {
        super(message);
    }
}