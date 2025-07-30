package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.gearnest.model.Feedback;
import com.example.gearnest.repository.FeedbackRepository;

@Controller
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepo;

    @GetMapping
    public String listFeedbacks(Model model) {
        model.addAttribute("title", "-Feedback");

        model.addAttribute("activePage", "feedback");
        model.addAttribute("feedbackList", feedbackRepo.findAll());
        return "admin/feedback-list";
    }

    @PostMapping("/admin/feedback/update")
    public String updateReviewed(@RequestParam("id") Long id,
            @RequestParam("reviewed") boolean reviewed) {
        System.out.println("Received ID: " + id + ", Reviewed: " + reviewed);

        Feedback feedback = feedbackRepo.findById(id).orElse(null);
        if (feedback != null) {
            feedback.setReviewed(reviewed);
            feedbackRepo.save(feedback);
            System.out.println("Saved feedback with reviewed = " + reviewed);
        } else {
            System.out.println("Feedback not found for ID = " + id);
        }
        return "redirect:/admin/feedback";
    }

    @PostMapping("/delete")
    public String deleteFeedback(@RequestParam("id") Long id) {
        feedbackRepo.deleteById(id);
        return "redirect:/admin/feedback";
    }

}
