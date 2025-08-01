package com.example.gearnest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.GarageProfileRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GarageList {

    @Autowired
    private GarageProfileRepository garageRepository;

    @GetMapping("/garages/app")
    public String showAllGarages(Model model) {
        List<Garage> garages = garageRepository.findAll();
        model.addAttribute("garages", garages);
        return "garage-list";
    }

    @GetMapping("/garages")
    public String showApprovedGarages(Model model) {
        List<Garage> garages = garageRepository.findByIsApprovedTrueAndStatus("Active");
        model.addAttribute("garages", garages);
        return "garage-list";
    }

    @GetMapping("/garage/{id}")
    public String viewGarageDetails(@PathVariable Long id, Model model, HttpSession session) {
        Garage garage = garageRepository.findById(id).orElse(null);
        if (garage == null) {
            session.setAttribute("error", "Garage not found");
            return "redirect:/garages";
        }

        model.addAttribute("garage", garage);
        return "garage-details";
    }

}
