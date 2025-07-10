package com.example.gearnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user-register")
    public String showUserRegisterForm(){
        return "user-register";
    }
}
