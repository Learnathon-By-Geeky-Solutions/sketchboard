package com.example.lostnfound.service.user;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UnknownIdentifierException;

public interface UserAccountService {
    void forgotPassword(final String email) throws UnknownIdentifierException;
    void updatePassword(final String password, final String token) throws InvalidTokenException, UnknownIdentifierException;
}
