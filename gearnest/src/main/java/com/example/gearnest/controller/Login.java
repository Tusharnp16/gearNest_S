package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.repository.AdminRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class Login {

    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private GarageRepository garageRepo;
    @Autowired
    private UserRepository userRepo;

    // @Autowired
    // private BCryptPasswordEncoder encoder;

    // @GetMapping("/")
    // public String redirectToLogin() {
    // return "redirect:/login-user";
    // }

    @GetMapping("/login-user")
    public String showLoginForm(Model model) {
        return "login-user"; // this should match your login-user.html template file
    }

    @PostMapping("/login-user")
    public String login(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            HttpSession session,
            Model model) {

        Object user = null;

        switch (role) {
            case "ADMIN":
                user = adminRepo.findByEmail(email);
                break;
            case "GARAGE":
                user = garageRepo.findByEmail(email);
                break;
            case "USER":
                user = userRepo.findByEmail(email);
                break;
        }

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "login-user";
        }

        /*
         * String hashedPassword = getPasswordFromUser(user);
         * 
         * if (!encoder.matches(password, hashedPassword)) {
         * model.addAttribute("error", "Invalid credentials.");
         * return "login";
         * }
         */

        session.setAttribute("loggedInUser", user);
        session.setAttribute("role", role);

        // Redirect to respective dashboard
        return "redirect:/" + role.toLowerCase() + "/dashboard";
    }

    /*
     * private String getPasswordFromUser(Object user) {
     * if (user instanceof Admin)
     * return ((Admin) user).getPassword();
     * if (user instanceof Garage)
     * return ((Garage) user).getPassword();
     * if (user instanceof AppUser)
     * return ((AppUser) user).getPassword(); // your user class
     * return "";
     * }
     */
}
