package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/")
    public String Home() {
        return "dashboard";
    }

    @GetMapping("/user/dashboard")
    public String home() {
        return "dashboard";
    }
}
