package com.example.lostnfound.controller;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.util.StringUtils;

@RestController
public class EmailVerificationController {

    @Value("${app.frontend.url}")
    String baseUrl;

    private final UserService userService;

    public EmailVerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verifyEmail")
    public RedirectView verifyUser(@RequestParam String token, RedirectAttributes redirectAttributes) {
        String RedirectUrl = baseUrl + "/login";

        if(StringUtils.isEmpty(token)) {
            redirectAttributes.addFlashAttribute("tokenError", "Invalid token - token is empty");
            //redirect to error page
            return new RedirectView("https://i.ibb.co.com/TDsrxVqr/9s8pju.jpg");
        }
        try {
            userService.verifyUser(token);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("tokenError", e.getMessage());
            return new RedirectView("https://i.ibb.co.com/TDsrxVqr/9s8pju.jpg");
        }
	    redirectAttributes.addFlashAttribute("message", "User successfully verified");
        return new RedirectView(RedirectUrl);
    }
}
