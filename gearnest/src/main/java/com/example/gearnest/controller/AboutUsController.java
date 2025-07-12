package com.example.gearnest.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutUsController {

    @GetMapping("/about")
    public String showAboutPage() {
        return "aboutus";  
    }
}

