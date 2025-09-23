package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Login {

    /**
     * Handles GET requests for the /login page.
     * This method's only job is to return the name of the view (HTML file) to be
     * rendered.
     * Spring Security handles all the form submission (POST) logic.
     * 
     * @return The name of the login view template ("login")
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // This should match your login HTML file name (login.html)
    }

    // You no longer need the @PostMapping("/login-user") method here.
    // It is completely handled by the Spring Security filter chain.
}
