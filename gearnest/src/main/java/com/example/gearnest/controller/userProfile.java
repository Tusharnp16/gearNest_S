package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class userProfile {

    @GetMapping("/profile")
    public String user_profile() {
        return "myprofile";
    }

}
