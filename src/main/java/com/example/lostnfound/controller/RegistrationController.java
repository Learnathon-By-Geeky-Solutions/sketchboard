package com.example.lostnfound.controller;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam String token, RedirectAttributes redirectAttributes) throws InvalidTokenException {
        if(StringUtils.isEmpty(token)) {
            redirectAttributes.addFlashAttribute("tokenError", "Invalid token");
            return REDIRECT_LOGIN;
        }
        try {
            userService.verifyUser(token);
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("Token is invalid or expired");
        }
        redirectAttributes.addFlashAttribute("message", "User successfully verified");
        return REDIRECT_LOGIN;
    }
}
