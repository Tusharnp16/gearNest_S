package com.example.gearnest.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.gearnest.model.Admin;
import com.example.gearnest.model.Garage;
import com.example.gearnest.model.PasswordResetToken;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.AdminRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.PasswordResetTokenRepository;
import com.example.gearnest.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private GarageRepository garageRepo;

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void requestPasswordReset(String email, String role, HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setRole(role);
        resetToken.setToken(token);
        resetToken.setExpiryDate(expiry);

        switch (role.toUpperCase()) {
            case "USER":
                User user = userRepo.findByEmail(email);
                if (user == null)
                    throw new RuntimeException("User not found");
                resetToken.setUser(user);
                break;
            case "ADMIN":
                Admin admin = adminRepo.findByEmail(email);
                if (admin == null)
                    throw new RuntimeException("Admin not found");
                resetToken.setAdmin(admin);
                break;
            case "GARAGE":
                Garage garage = garageRepo.findByEmail(email);
                if (garage == null)
                    throw new RuntimeException("Garage not found");
                resetToken.setGarage(garage);
                break;
            default:
                throw new RuntimeException("Invalid role");
        }

        tokenRepo.save(resetToken);

        String appUrl = request.getScheme() + "://" + request.getServerName()
                + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? ""
                        : ":" + request.getServerPort());

        String resetLink = appUrl + "/reset-password-form?token=" + token + "&role=" + role;
        sendEmail(email, resetLink);
    }

    private void sendEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("GearNest - Password Reset Request");
        message.setText("Click the following link to reset your password:\n" + resetLink +
                "\n\nThis link is valid for 1 hour.\n\nIf you did not request a reset, please ignore this email.");
        mailSender.send(message);
    }

    public boolean resetPassword(String token, String role, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token).orElse(null);
        if (resetToken == null || resetToken.isExpired()) {
            return false;
        }
        System.out.println("##################" + role);
        String encodedPassword = passwordEncoder.encode(newPassword);

        switch (role.toUpperCase()) {
            case "USER":
                User user = resetToken.getUser();
                if (user == null)
                    return false;
                user.setPassword(encodedPassword);
                userRepo.save(user);
                break;
            case "ADMIN":
                Admin admin = resetToken.getAdmin();
                if (admin == null)
                    return false;
                admin.setPassword(encodedPassword);
                adminRepo.save(admin);
                break;
            case "GARAGE":
                Garage garage = resetToken.getGarage();
                if (garage == null)
                    return false;
                garage.setPassword(encodedPassword);
                garageRepo.save(garage);
                break;
            default:
                return false;
        }

        tokenRepo.delete(resetToken);
        return true;
    }

}
