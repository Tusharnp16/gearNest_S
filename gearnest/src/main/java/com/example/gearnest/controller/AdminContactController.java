package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.gearnest.model.ContactMessage;
import com.example.gearnest.model.Feedback;
import com.example.gearnest.repository.ContactMessageRepository;

@Controller
@RequestMapping("/admin/contact")
public class AdminContactController {

    @Autowired
    private ContactMessageRepository messageRepo;

    @GetMapping
    public String listMessages(Model model) {
        model.addAttribute("contactList", messageRepo.findAll());
        return "admin/contact-list";
    }

    @PostMapping("/respond")
    public String respondToMessage(@RequestParam("id") Long id) {
        ContactMessage message = messageRepo.findById(id).orElse(null);
        if (message != null) {
            message.setResponded(true);
            messageRepo.save(message);
        }
        return "redirect:/admin/contact";
    }

    @PostMapping("/delete")
    public String deleteMessage(@RequestParam("id") Long id) {
        messageRepo.deleteById(id);
        return "redirect:/admin/contact";
    }
}
