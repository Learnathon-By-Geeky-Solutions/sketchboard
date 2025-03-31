package com.example.lostnfound.service.user;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UnknownIdentifierException;
import com.example.lostnfound.exception.UserNotFoundException;

public interface UserAccountService {
    void forgotPassword(final String email) throws UnknownIdentifierException, UserNotFoundException;
    void updatePassword(final String password, final String token) throws InvalidTokenException, UnknownIdentifierException;
}
