package com.example.gearnest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.GarageServices;
import com.example.gearnest.repository.GarageServicesRepository;

@Controller
@RequestMapping("/admin/services")
public class AdminServicesList {

    @Autowired
    private GarageServicesRepository garageServicesRepository;

    @GetMapping
    public String services(Model model) {
        model.addAttribute("title", "-Services");
        model.addAttribute("activePage", "services");
        model.addAttribute("servicesList", garageServicesRepository.findAll());
        model.addAttribute("newService", new GarageServices()); // for modal binding
        return "admin/services-list";
    }

    @PostMapping("/add")
    public String addService(@ModelAttribute GarageServices newService, RedirectAttributes redirectAttributes) {
        garageServicesRepository.save(newService);
        redirectAttributes.addFlashAttribute("serviceSuccess", true);
        return "redirect:/admin/services";
    }

    @GetMapping("/api/services/check-name")
    @ResponseBody
    public Map<String, Boolean> checkServiceName(@RequestParam String name) {
        boolean exists = garageServicesRepository.existsByNameIgnoreCase(name.trim());
        return Map.of("exists", exists);
    }

    @PostMapping("/update")
    public String updateService(@RequestParam Long id, @RequestParam String name,
            RedirectAttributes redirectAttributes) {
        GarageServices service = garageServicesRepository.findById(id).orElse(null);
        if (service != null) {
            service.setName(name.trim());
            garageServicesRepository.save(service);
            redirectAttributes.addFlashAttribute("serviceUpdated", true);
        }
        return "redirect:/admin/services";
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteService(@RequestParam Long id) {
        if (garageServicesRepository.existsById(id)) {
            garageServicesRepository.deleteById(id);
            return ResponseEntity.ok().body(Map.of("success", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Service not found"));
        }
    }

}