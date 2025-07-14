package com.example.gearnest.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.GarageProfileRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GarageController {

    @Autowired
    private GarageProfileRepository garageRepository;

    @GetMapping("/garage/register")
    public String showGarageForm(Model model, HttpSession session) {
        model.addAttribute("garage", new Garage());

        model.addAttribute("success", session.getAttribute("success"));
        model.addAttribute("error", session.getAttribute("error"));
        session.removeAttribute("success");
        session.removeAttribute("error");

        return "garage-register";
    }

    @PostMapping("/garage/register")
    public String registerGarage(@ModelAttribute Garage garage,
            @RequestParam("logoBase64") String logoBase64,
            HttpSession session) {
        try {
            // Save base64 to file
            String logoPath = saveBase64ImageToFile(logoBase64, "uploads/logos/");
            garage.setLogoPath(logoPath);

            // Set rest details
            garage.setCreatedAt(LocalDateTime.now());
            garage.setApproved(false);
            garage.setStatus("Pending");
            garageRepository.save(garage);

            session.setAttribute("success", "Garage registered successfully!");
            return "redirect:/garage/register";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Something went wrong.");
            return "redirect:/garage/register";
        }
    }

    private String saveBase64ImageToFile(String base64Image, String uploadDir) throws IOException {
        if (base64Image == null || base64Image.isBlank())
            return null;

        String[] parts = base64Image.split(",");
        String imageData = parts[1];
        byte[] decoded = java.util.Base64.getDecoder().decode(imageData);

        String fileName = "logo_" + System.currentTimeMillis() + ".png";
        File uploadPath = new File("uploads/logos");
        if (!uploadPath.exists())
            uploadPath.mkdirs();

        File file = new File(uploadPath, fileName);
        java.nio.file.Files.write(file.toPath(), decoded);

        return "/" + uploadDir + fileName;
    }

    

}