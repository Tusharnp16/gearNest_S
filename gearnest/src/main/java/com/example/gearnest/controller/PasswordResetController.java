package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.services.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService resetService;

    @GetMapping("/reset-password")
    public String showRequestForm() {
        return "reset-password-request";
    }

    @PostMapping("/reset-password")
    public String handleRequest(@RequestParam String email,
            @RequestParam String role,
            HttpServletRequest request,
            Model model) {
        System.out.println("**************************" + role);
        try {
            resetService.requestPasswordReset(email, role, request); // 🔧 role-aware
            model.addAttribute("success", true);
            model.addAttribute("message", "Check your email for the reset link.");
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("error", "No account found for that email in selected module.");
        }
        return "reset-password-request";
    }

    @GetMapping("/reset-password-form")
    public String showResetForm(@RequestParam String token,
            @RequestParam String role,
            Model model) {
        model.addAttribute("token", token);
        model.addAttribute("role", role); // 👈 pass back to form
        return "reset-password-form";
    }

    @PostMapping("/reset-password-form")
    public String handleReset(@RequestParam String token,
            @RequestParam String role,
            @RequestParam String password,
            Model model) {
        boolean success = resetService.resetPassword(token, role, password); // 🔧 updated method

        model.addAttribute("token", token);
        model.addAttribute("role", role);

        if (success) {
            model.addAttribute("success", true);
            model.addAttribute("message", "Password reset successful!");
        } else {
            model.addAttribute("success", false);
            model.addAttribute("error", "Invalid or expired token.");
        }

        return "reset-password-form";
    }
}
