package com.example.gearnest.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Garage;
import com.example.gearnest.model.GarageServices;
import com.example.gearnest.model.ParticularGarageService;
import com.example.gearnest.model.VehicleType;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.GarageServicesRepository;
import com.example.gearnest.repository.ParticularGarageServiceRepository;
import com.example.gearnest.repository.VehicleTypeRepository;

@Controller
@RequestMapping("/garage")
public class ParticularGarageServiceController {

    @Autowired
    private GarageRepository garageRepo;
    @Autowired
    private GarageServicesRepository garageServicesRepo;
    @Autowired
    private ParticularGarageServiceRepository particularServiceRepo;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepo;

    @GetMapping("/services")
    public String showServicesPage(Model model, Principal principal) {
        Garage loggedInGarage = garageRepo.findByEmail(principal.getName());
        List<ParticularGarageService> offeredServices = particularServiceRepo.findByGarageId(loggedInGarage.getId());

        List<GarageServices> allMasterServices = garageServicesRepo.findAll();
        List<VehicleType> allVehicleTypes = vehicleTypeRepo.findAll();

        allMasterServices.sort(Comparator.comparing(GarageServices::getName));
        allVehicleTypes.sort(Comparator.comparing(VehicleType::getName));

        model.addAttribute("garage", loggedInGarage);
        model.addAttribute("offeredServices", offeredServices);
        model.addAttribute("availableServices", allMasterServices);
        model.addAttribute("vehicleTypes", allVehicleTypes);
        model.addAttribute("newService", new ParticularGarageService());
        model.addAttribute("title", "Services");
        model.addAttribute("activePage", "services");

        return "garage/service";
    }

    @PostMapping("/services/add")
    public String addService(@ModelAttribute ParticularGarageService newService, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Garage loggedInGarage = garageRepo.findByEmail(principal.getName());
            if (loggedInGarage == null) {
                redirectAttributes.addFlashAttribute("error", "Garage not found.");
                return "redirect:/garage/services";
            }

            newService.setGarage(loggedInGarage);

            if (newService.getService() == null || newService.getVehicleType() == null) {
                redirectAttributes.addFlashAttribute("error", "Please select a service and vehicle type.");
                return "redirect:/garage/services";
            }

            boolean exists = particularServiceRepo.existsByGarageIdAndServiceIdAndVehicleTypeId(
                    loggedInGarage.getId(),
                    newService.getService().getId(),
                    newService.getVehicleType().getId());

            if (exists) {
                redirectAttributes.addFlashAttribute("error", "This service for this vehicle type already exists.");
                return "redirect:/garage/services";
            }

            particularServiceRepo.save(newService);
            redirectAttributes.addFlashAttribute("success", "Service added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while adding the service.");
        }
        return "redirect:/garage/services";
    }

    @GetMapping("/services/edit/{id}")
    public String showEditServiceForm(@PathVariable("id") Long id, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {
        Garage loggedInGarage = garageRepo.findByEmail(principal.getName());
        Optional<ParticularGarageService> optionalService = particularServiceRepo.findById(id);

        if (optionalService.isPresent()) {
            ParticularGarageService serviceToEdit = optionalService.get();
            if (serviceToEdit.getGarage().equals(loggedInGarage)) {
                model.addAttribute("newService", serviceToEdit);
                model.addAttribute("availableServices", garageServicesRepo.findAll());
                model.addAttribute("vehicleTypes", vehicleTypeRepo.findAll());
                model.addAttribute("title", "Edit Service");
                model.addAttribute("activePage", "services");
                return "garage/service_edit";
            } else {
                redirectAttributes.addFlashAttribute("error", "You do not have permission to edit this service.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Service not found.");
        }
        return "redirect:/garage/services";
    }

    @PostMapping("/services/update/{id}")
    public String updateService(@PathVariable("id") Long id, @ModelAttribute ParticularGarageService updatedService,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Garage loggedInGarage = garageRepo.findByEmail(principal.getName());
            Optional<ParticularGarageService> optionalService = particularServiceRepo.findById(id);

            if (optionalService.isPresent()) {
                ParticularGarageService existingService = optionalService.get();
                if (existingService.getGarage().equals(loggedInGarage)) {
                    existingService.setFee(updatedService.getFee());
                    existingService.setVehicleType(updatedService.getVehicleType());

                    particularServiceRepo.save(existingService);
                    redirectAttributes.addFlashAttribute("success", "Service updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "You do not have permission to update this service.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Service not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the service.");
        }
        return "redirect:/garage/services";
    }

    @GetMapping("/services/toggle/{id}")
    public String toggleServiceStatus(@PathVariable("id") Long id, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Garage loggedInGarage = garageRepo.findByEmail(principal.getName());
            Optional<ParticularGarageService> optionalService = particularServiceRepo.findById(id);

            if (optionalService.isPresent()) {
                ParticularGarageService serviceToUpdate = optionalService.get();
                if (serviceToUpdate.getGarage().equals(loggedInGarage)) {
                    serviceToUpdate.setActive(!serviceToUpdate.isActive());
                    particularServiceRepo.save(serviceToUpdate);
                    redirectAttributes.addFlashAttribute("success", "Service status updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "You do not have permission to modify this service.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Service not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while toggling the service status.");
        }
        return "redirect:/garage/services";
    }

    @GetMapping("/services/delete/{id}")
    public String deleteService(@PathVariable("id") Long id, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Garage loggedInGarage = garageRepo.findByEmail(principal.getName());
            Optional<ParticularGarageService> optionalService = particularServiceRepo.findById(id);

            if (optionalService.isPresent()) {
                ParticularGarageService serviceToDelete = optionalService.get();
                if (serviceToDelete.getGarage().equals(loggedInGarage)) {
                    particularServiceRepo.deleteById(id);
                    redirectAttributes.addFlashAttribute("success", "Service deleted successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "You do not have permission to delete this service.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Service not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the service.");
        }
        return "redirect:/garage/services";
    }
}
