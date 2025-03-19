package com.example.lostnfound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    /**
     * Handles the root URL request and returns the index view.
     *
     * @return A string representing the name of the view to be rendered for the home page
     */
    @GetMapping("/")
    public String index() {
        //redirect to swagger
        return "redirect:/swagger-ui.html";
    }

}
