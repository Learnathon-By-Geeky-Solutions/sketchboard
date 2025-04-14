package com.example.lostnfound.controller;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.util.StringUtils;

@RestController
public class EmailVerificationController {

    private static final String RedirectUrl = "http://lostnfoundbd.duckdns.org/login";
    private final UserService userService;

    public EmailVerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verifyEmail")
    public RedirectView verifyUser(@RequestParam String token, RedirectAttributes redirectAttributes) throws InvalidTokenException {
        if(StringUtils.isEmpty(token)) {
            redirectAttributes.addFlashAttribute("tokenError", "Invalid token - token is empty");
            //redirect to login page
            return new RedirectView(RedirectUrl);
        }
        try {
            userService.verifyUser(token);
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("Token is invalid or expired");
        } catch (UserNotFoundException e) {
	        throw new RuntimeException(e);
        }
	    redirectAttributes.addFlashAttribute("message", "User successfully verified");
        return new RedirectView(RedirectUrl);
    }
}
