package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.model.User;
import com.example.gearnest.repository.UserRepository;
import com.example.gearnest.services.OtpService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/register")
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email, HttpSession session) {
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(email, otp);

        // Save OTP and email in session
        session.setAttribute("otp", otp);
        session.setAttribute("otpEmail", email);

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/submit")
    public ResponseEntity<?> register(@RequestBody User user, HttpSession session) {
        String sessionOtp = (String) session.getAttribute("otp");
        String sessionEmail = (String) session.getAttribute("otpEmail");

        // Validate OTP and email
        if (sessionOtp == null || !sessionOtp.equals(user.getOtp()) || sessionEmail == null
                || !sessionEmail.equals(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        // Save as verified
        user.setIsVerified(true);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setProfilePic("default.jpg");

        userRepository.save(user);

        // Clear session OTP after successful registration
        session.removeAttribute("otp");
        session.removeAttribute("otpEmail");

        return ResponseEntity.ok("User registered successfully");
    }

}