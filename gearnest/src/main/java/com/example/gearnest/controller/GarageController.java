package com.example.gearnest.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.GarageServicesRepository;
import com.example.gearnest.services.OtpService;

import jakarta.servlet.http.HttpSession;

@Controller
public class GarageController {

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private GarageServicesRepository garageServicesRepository;

    // ==== Page Rendering ====

    @GetMapping("/garage-register")
    public String showGarageForm(Model model, HttpSession session) {
        model.addAttribute("garage", new Garage());

        model.addAttribute("success", session.getAttribute("success"));
        model.addAttribute("error", session.getAttribute("error"));
        session.removeAttribute("success");
        session.removeAttribute("error");

        return "garage-register";
    }

    // ==== Real-Time AJAX Validation ====

    @PostMapping("/api/garage/register/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(garageRepository.existsByEmail(email));
    }

    @PostMapping("/api/garage/register/check-phone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam String phone) {
        return ResponseEntity.ok(garageRepository.existsByPhno(phone));
    }

    // ==== Send OTP ====

    @PostMapping("/api/garage/register/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email, HttpSession session) {
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(email, otp);
        session.setAttribute("garageOtp", otp);
        session.setAttribute("garageOtpEmail", email);
        return ResponseEntity.ok("OTP sent successfully");
    }

    // ==== Final Submission ====
    @PostMapping("/api/garage/register/submit")
    public ResponseEntity<?> registerGarage(@RequestBody Garage garage, HttpSession session) {
        try {
            // OTP validation
            String sessionOtp = (String) session.getAttribute("garageOtp");
            String sessionEmail = (String) session.getAttribute("garageOtpEmail");

            if (sessionOtp == null || !sessionOtp.equals(garage.getOtp()) ||
                    sessionEmail == null || !sessionEmail.equals(garage.getEmail())) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }

            // Handle logo
            String base64 = garage.getLogoPath();
            if (base64 != null && base64.startsWith("data:image")) {
                String logoPath = saveBase64ImageToFile(base64, "uploads/logos/");
                garage.setLogoPath(logoPath);
            }

            // Encrypt password
            garage.setPassword(new BCryptPasswordEncoder().encode(garage.getPassword()));

            // Metadata
            garage.setCreatedAt(LocalDateTime.now());
            garage.setApproved(false);
            garage.setStatus("Pending");
            garage.setVerified(true);
            garage.setRating(5.0);

            // Lookup and assign services
            if (garage.getServiceIds() != null && !garage.getServiceIds().isEmpty()) {
                garage.setServicesOffered(garageServicesRepository.findAllById(garage.getServiceIds()));
            }

            garageRepository.save(garage);

            session.removeAttribute("garageOtp");
            session.removeAttribute("garageOtpEmail");

            return ResponseEntity.ok("Garage registered successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    // ==== Utility: Save Base64 image to file ====

    private String saveBase64ImageToFile(String base64Image, String uploadDir) throws IOException {
        if (base64Image == null || base64Image.isBlank())
            return null;

        if (!base64Image.contains(",")) {
            throw new IllegalArgumentException("Invalid Base64 image format");
        }

        String[] parts = base64Image.split(",");
        String imageData = parts[1];
        byte[] decoded = Base64.getDecoder().decode(imageData);

        String fileName = "logo_" + System.currentTimeMillis() + ".png";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists())
            uploadPath.mkdirs();

        File file = new File(uploadPath, fileName);
        java.nio.file.Files.write(file.toPath(), decoded);

        return "/" + uploadDir + fileName;
    }

    @GetMapping("/garage/dashboard")
    public String dashboard(Model model) {
        // model.addAttribute();
        return "dashboard";
    }
}
