package com.example.gearnest.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.gearnest.model.Admin;
import com.example.gearnest.model.Garage;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.AdminRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Inject repositories to fetch the full user object
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private GarageRepository garageRepo;
    @Autowired
    private UserRepository userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        HttpSession session = request.getSession();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectURL = "/";

        // Based on the role, fetch the complete user object and store it in the session
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                Admin admin = adminRepo.findByEmail(email);
                session.setAttribute("loggedInUser", admin); // Set Admin object
                redirectURL = "/admin/dashboard";
                break;
            } else if (role.equals("ROLE_GARAGE")) {
                Garage garage = garageRepo.findByEmail(email);
                session.setAttribute("loggedInUser", garage); // Set Garage object
                redirectURL = "/garage/dashboard";
                break;
            } else if (role.equals("ROLE_USER")) {
                User user = userRepo.findByEmail(email);
                session.setAttribute("loggedInUser", user); // Set User object
                redirectURL = "/user/dashboard";
                break;
            }
        }

        response.sendRedirect(redirectURL);
    }
}
