package com.example.gearnest.controller;

import com.example.gearnest.dto.GarageRegistrationDto;
import com.example.gearnest.model.*;
import com.example.gearnest.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRepository userRepo;
    private final GarageProfileRepository garageRepo;
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/register/garage")
    public String garageRegisterForm(Model model) {
        model.addAttribute("garage", new GarageRegistrationDto());
        return "garage-register";
    }

    @PostMapping("/register/garage")
    public String registerGarage(@ModelAttribute("garage") GarageRegistrationDto dto) {
        User user = new User();
        user.setName(dto.getOwnerName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.GARAGE);
        userRepo.save(user);

        GarageProfile profile = new GarageProfile();
        profile.setUser(user);
        profile.setGarageName(dto.getGarageName());
        profile.setAddress(dto.getAddress());
        profile.setContact(dto.getContact());
        profile.setDescription(dto.getDescription());
        garageRepo.save(profile);

        return "redirect:/login";
    }
}