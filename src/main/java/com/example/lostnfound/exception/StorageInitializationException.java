package com.example.lostnfound.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageInitializationException extends RuntimeException {

    public StorageInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
