package com.example.gearnest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.gearnest.model.Admin;
import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.AdminRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private GarageRepository garageRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // The 'username' parameter now contains the combined "ROLE:email" string.

        String[] parts = username.split(":", 2);
        if (parts.length != 2) {
            throw new UsernameNotFoundException("Username format must be 'ROLE:email'");
        }

        String role = parts[0];
        String email = parts[1];

        switch (role) {
            case "ADMIN":
                Admin admin = adminRepo.findByEmail(email);
                if (admin != null) {
                    return org.springframework.security.core.userdetails.User
                            .withUsername(admin.getEmail())
                            .password(admin.getPassword()) // Ensure this is encoded in your DB
                            .roles("ADMIN")
                            .build();
                }
                break;

            case "GARAGE":
                Garage garage = garageRepo.findByEmail(email);
                if (garage != null) {
                    return org.springframework.security.core.userdetails.User
                            .withUsername(garage.getEmail())
                            .password(garage.getPassword())
                            .roles("GARAGE")
                            .build();
                }
                break;

            case "USER":
                // Use the fully qualified name to avoid conflict with Spring's User class
                com.example.gearnest.model.User user = userRepo.findByEmail(email);
                if (user != null) {
                    return org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password(user.getPassword())
                            .roles("USER")
                            .build();
                }
                break;
        }

        throw new UsernameNotFoundException("User with email '" + email + "' not found for role '" + role + "'");
    }
}
