package com.example.gearnest.controller;


import com.example.gearnest.model.ContactMessage;
import com.example.gearnest.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
public class ContactController {

    @Autowired
    private ContactMessageRepository contactRepo;

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contactus";
    }

    @PostMapping("/contact")
    public String submitContactForm(@ModelAttribute ContactMessage contactMessage, Model model) {
        contactRepo.save(contactMessage);
        model.addAttribute("success", "Thank you for contacting us!");
        model.addAttribute("contactMessage", new ContactMessage());
        return "contactus";
    }
}

