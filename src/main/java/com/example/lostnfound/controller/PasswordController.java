package com.example.lostnfound.controller;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UnknownIdentifierException;
import com.example.lostnfound.service.user.UserAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PasswordController {

    private final UserAccountService userAccountService;

    public PasswordController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam String email) throws UnknownIdentifierException {
        try {
            userAccountService.forgotPassword(email);
        } catch (UnknownIdentifierException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }

    @PostMapping("/reset-password")
    public String updatePassword(@RequestParam String token, @RequestParam String password) throws InvalidTokenException, UnknownIdentifierException {
        try {
            userAccountService.updatePassword(password, token);
        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }

}
