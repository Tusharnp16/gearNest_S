package com.example.gearnest.controller;

import com.example.gearnest.model.Feedback;
import com.example.gearnest.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/feedback")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@ModelAttribute Feedback feedback, Model model) {
        feedbackRepository.save(feedback);
        model.addAttribute("success", "Thanks for your valuable feedback!");
        model.addAttribute("feedback", new Feedback()); 
        return "feedback";
    }
}

