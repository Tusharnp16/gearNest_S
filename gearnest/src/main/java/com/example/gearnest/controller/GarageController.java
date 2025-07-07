package com.example.gearnest.controller;

import com.example.gearnest.model.Garage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GarageController {

    @GetMapping("/garage-register")
    public String showGarageRegisterForm(Model model) {
        model.addAttribute("garage", new Garage());
        return "garage-register";
    }
}
