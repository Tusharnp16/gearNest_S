package com.example.gearnest.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.CityRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.StateRepository;
import com.example.gearnest.services.FileStorageService;

@Controller
@RequestMapping("/garage/profile")
public class GarageProfileController {

    private static final Logger logger = LoggerFactory.getLogger(GarageProfileController.class);

    @Autowired
    private GarageRepository garageRepo;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @GetMapping
    public String showProfilePage(Model model, Principal principal) {
        Garage garage = garageRepo.findByEmail(principal.getName());
        if (garage == null) {
            return "redirect:/login";
        }

        model.addAttribute("garage", garage);
        model.addAttribute("title", "Profile");
        model.addAttribute("activePage", "profile");

        // Defaults for opening hours
        Integer openHour = 9;
        String openPeriod = "AM";
        Integer closeHour = 5;
        String closePeriod = "PM";

        String openingHours = garage.getOpeningHours();
        if (openingHours != null && !openingHours.isBlank()) {
            Pattern p = Pattern.compile("(?i)(\\d{1,2})(?::\\d{2})?\\s*(AM|PM)");
            Matcher m = p.matcher(openingHours);
            List<String> tokens = new ArrayList<>();
            while (m.find()) {
                tokens.add(m.group(1));
                tokens.add(m.group(2).toUpperCase());
            }
            if (tokens.size() >= 4) {
                openHour = Integer.valueOf(tokens.get(0));
                openPeriod = tokens.get(1);
                closeHour = Integer.valueOf(tokens.get(2));
                closePeriod = tokens.get(3);
            } else if (tokens.size() >= 2) {
                openHour = Integer.valueOf(tokens.get(0));
                openPeriod = tokens.get(1);
            }
        }

        model.addAttribute("openHour", openHour);
        model.addAttribute("openPeriod", openPeriod);
        model.addAttribute("closeHour", closeHour);
        model.addAttribute("closePeriod", closePeriod);

        // Save State/City IDs for frontend
        model.addAttribute("savedState", garage.getState() != null ? garage.getState().getId() : null);
        model.addAttribute("savedCity", garage.getCity() != null ? garage.getCity().getId() : null);
        return "garage/profile";
    }

    @PostMapping("/upload-profile-image")
    public String uploadProfileImage(@RequestParam("profileImageFile") MultipartFile file,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Garage garage = garageRepo.findByEmail(principal.getName());
            if (garage == null) {
                redirectAttributes.addFlashAttribute("error", "Garage not found.");
                return "redirect:/garage/profile";
            }

            if (garage.getProfileImage() != null && !garage.getProfileImage().equals("default.png")) {
                fileStorageService.deleteFile(garage.getProfileImage(), "garage-profiles");
            }

            String filename = fileStorageService.storeFile(file, "garage-profiles");
            garage.setProfileImage(filename);
            garageRepo.save(garage);

            redirectAttributes.addFlashAttribute("success", "Profile picture updated successfully!");
        } catch (Exception e) {
            logger.error("Error uploading profile image", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload profile picture!");
        }
        return "redirect:/garage/profile";
    }

    @PostMapping("/upload-card-image")
    public String uploadCardImage(@RequestParam("cardImageFile") MultipartFile file,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Garage garage = garageRepo.findByEmail(principal.getName());
            if (garage == null) {
                redirectAttributes.addFlashAttribute("error", "Garage not found.");
                return "redirect:/garage/profile";
            }

            if (garage.getCardImage() != null && !garage.getCardImage().equals("default.png")) {
                fileStorageService.deleteFile(garage.getCardImage(), "garage-cards");
            }

            String filename = fileStorageService.storeFile(file, "garage-cards");
            garage.setCardImage(filename);
            garageRepo.save(garage);

            redirectAttributes.addFlashAttribute("success", "Card image updated successfully!");
        } catch (Exception e) {
            logger.error("Error uploading card image", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload card image!");
        }
        return "redirect:/garage/profile";
    }

    // ---------------- JSON-based Update -----------------
    @PostMapping("/update-details-json")
    public ResponseEntity<?> updateGarageDetailsJson(@RequestBody Garage requestGarage,
            Principal principal) {

        Garage garage = garageRepo.findByEmail(principal.getName());
        if (garage == null) {
            return ResponseEntity.badRequest().body("Garage not found");
        }

        garage.setGarageName(requestGarage.getGarageName());
        garage.setOwnerName(requestGarage.getOwnerName());
        garage.setPhno(requestGarage.getPhno());
        garage.setAddress(requestGarage.getAddress());
        garage.setDescription(requestGarage.getDescription());
        garage.setOpeningHours(requestGarage.getOpeningHours());

        // Set State by ID
        if (requestGarage.getState() != null && requestGarage.getState().getId() != null) {
            stateRepository.findById(requestGarage.getState().getId())
                    .ifPresent(garage::setState);
        }

        // Set City by ID
        if (requestGarage.getCity() != null && requestGarage.getCity().getId() != null) {
            cityRepository.findById(requestGarage.getCity().getId())
                    .ifPresent(garage::setCity);
        }

        garageRepo.save(garage);
        return ResponseEntity.ok("Garage details updated successfully");
    }

    @GetMapping("/api/garage/check-phone")
    @ResponseBody
    public boolean checkPhoneExists(@RequestParam String phno, Principal principal) {
        // Get logged-in garage
        Garage currentGarage = garageRepo.findByEmail(principal.getName());

        // Allow if same as current phone
        if (currentGarage != null && currentGarage.getPhno().equals(phno)) {
            return false;
        }

        return garageRepo.existsByPhno(phno);
    }
}
