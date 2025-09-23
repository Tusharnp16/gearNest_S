package com.example.gearnest.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.GarageRepository;

@Controller
@RequestMapping("/garage")
public class GarageDashboardController {

    @Autowired
    private GarageRepository garageRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        String username = principal.getName();
        Garage loggedInGarage = garageRepository.findByEmail(username);
        model.addAttribute("garage", loggedInGarage);
        model.addAttribute("title", "Dashboard");
        model.addAttribute("activePage", "dashboard");
        return "garage/garage-dashboard";
    }

}