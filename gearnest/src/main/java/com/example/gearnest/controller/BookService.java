package com.example.gearnest.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.Garage;
import com.example.gearnest.model.ParticularGarageService;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.ParticularGarageServiceRepository;
import com.example.gearnest.repository.StateRepository;
import com.example.gearnest.repository.UserRepository;

@Controller
public class BookService {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private GarageRepository garageRepository;
    @Autowired
    private ParticularGarageServiceRepository particularGarageServiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @GetMapping("/user/book-service/{garageId}")
    public String bookservicePage(@PathVariable("garageId") Long garageId, Model model,
            RedirectAttributes redirectAttributes, Principal principal) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to book a service.");
            return "redirect:/login";
        }

        Optional<Garage> optionalGarage = garageRepository.findById(garageId);
        if (optionalGarage.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Garage not found.");
            return "redirect:/dashboard";
        }

        Garage garage = optionalGarage.get();
        List<ParticularGarageService> garageServices = particularGarageServiceRepository.findByGarageId(garageId);

        model.addAttribute("title", "Book Service");
        model.addAttribute("garage", garage);
        model.addAttribute("states", stateRepository.findAll());
        model.addAttribute("vehicleTypes",
                particularGarageServiceRepository.findDistinctVehicleTypesByGarageId(garageId));
        model.addAttribute("garageServices", garageServices);
        model.addAttribute("user", userRepository.findByEmail(principal.getName()));
        model.addAttribute("razorpayKeyId", razorpayKeyId);

        model.addAttribute("booking", new Booking());

        return "bookservice";
    }

    // The @PostMapping for form submission is now handled by PaymentController.
}