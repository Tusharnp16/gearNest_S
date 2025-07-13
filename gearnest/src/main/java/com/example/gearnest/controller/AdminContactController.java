package com.example.gearnest.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

import com.example.gearnest.model.ContactMessage;
// import com.example.gearnest.model.Feedback;
import com.example.gearnest.repository.ContactMessageRepository;
import com.example.gearnest.utills.ExcelExporter;
import com.example.gearnest.utills.PdfExport;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/contact")
public class AdminContactController {

    @Autowired
    private ContactMessageRepository messageRepo;

    @Autowired
    private JavaMailSender mailSender;

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

    @PostMapping("/respond-email")
    public String sendEmailResponse(@RequestParam("id") Long id,
            @RequestParam("message") String responseText) {
        ContactMessage message = messageRepo.findById(id).orElse(null);
        if (message != null) {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(message.getEmail());
            mail.setSubject("Response to your Contact Message: " + message.getSubject());
            mail.setText("Dear " + message.getName() + ",\n\n" + responseText + "\n\nRegards,\nGearNest Team");

            mailSender.send(mail);

            message.setResponded(true);
            messageRepo.save(message);
        }
        return "redirect:/admin/contact";
    }

    @GetMapping("/filter")
    public String filter(@RequestParam("type") String type, Model model) {
        List<ContactMessage> filtered;
        if ("responded".equals(type)) {
            filtered = messageRepo.findByResponded(true);
        } else if ("unresponded".equals(type)) {
            filtered = messageRepo.findByResponded(false);
        } else {
            filtered = messageRepo.findAll();
        }

        model.addAttribute("contactList", filtered);
        return "admin/contact-list";
    }

     @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=contact_messages.xlsx";
        response.setHeader(headerKey, headerValue);

        List<ContactMessage> messages = messageRepo.findAll();
        ExcelExporter.export(messages, response);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=contact_messages.pdf";
        response.setHeader(headerKey, headerValue);

        List<ContactMessage> messages = messageRepo.findAll();
        PdfExport.export(messages, response);
    }

}
