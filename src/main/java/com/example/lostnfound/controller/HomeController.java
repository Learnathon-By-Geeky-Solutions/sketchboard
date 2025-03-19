package com.example.lostnfound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
public class HomeController {
    /**
     * Handles the root URL request and returns the index view.
     *
     * @return A string representing the name of the view to be rendered for the home page
     */
    @GetMapping("/")
    @Tag(name = "Home APIs", description = "REST APIs related to home page.")
    public String index() {
        //redirect to swagger
        return "redirect:/swagger-ui.html";
    }

}
