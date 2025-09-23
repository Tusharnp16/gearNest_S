package com.example.gearnest.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.gearnest.model.VehicleType;
import com.example.gearnest.repository.VehicleTypeRepository;

@Controller
@RequestMapping("/admin/vehicletypes")
public class AdminVehicleTypeController {

    @Autowired
    private VehicleTypeRepository vehicleTypeRepo;

    @GetMapping
    public String viewVehicleTypes(Model model) {
        List<VehicleType> vehicleTypes = vehicleTypeRepo.findAll();
        model.addAttribute("vehicleTypes", vehicleTypes);
        model.addAttribute("newVehicleType", new VehicleType());
        model.addAttribute("title", "Vehicle Types");
        model.addAttribute("activePage", "vehicletypes");
        return "admin/vehicle-types";
    }

    @PostMapping("/add")
    public ResponseEntity<?> addVehicleType(@ModelAttribute VehicleType newVehicleType) {
        // Server-side validation to prevent duplicates
        if (vehicleTypeRepo.existsByNameIgnoreCase(newVehicleType.getName())) {
            return ResponseEntity.status(409).body(Map.of("error", "This vehicle type already exists."));
        }
        vehicleTypeRepo.save(newVehicleType);
        return ResponseEntity.ok(Map.of("message", "Vehicle Type added successfully!"));
    }

    @GetMapping("/check-name")
    @ResponseBody
    public Map<String, Boolean> checkName(@RequestParam String name) {
        boolean exists = vehicleTypeRepo.existsByNameIgnoreCase(name);
        return Map.of("exists", exists);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateVehicleType(@ModelAttribute VehicleType vehicleType) {
        Optional<VehicleType> existingOpt = vehicleTypeRepo.findById(vehicleType.getId());
        if (existingOpt.isPresent()) {
            VehicleType existingVehicleType = existingOpt.get();
            // Only check for duplicates if the name has actually been changed
            if (!existingVehicleType.getName().equalsIgnoreCase(vehicleType.getName())) {
                if (vehicleTypeRepo.existsByNameIgnoreCase(vehicleType.getName())) {
                    return ResponseEntity.status(409).body(Map.of("error", "This vehicle type already exists."));
                }
            }
        }
        vehicleTypeRepo.save(vehicleType);
        return ResponseEntity.ok(Map.of("message", "Vehicle Type updated successfully!"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVehicleType(@PathVariable("id") Long id) {
        try {
            vehicleTypeRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Vehicle Type deleted successfully!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(400).body(Map.of("error", "Cannot delete: This vehicle type is in use."));
        }
    }
}