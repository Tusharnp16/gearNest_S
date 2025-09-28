package com.example.gearnest.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gearnest.model.GarageServices;
import com.example.gearnest.repository.GarageServicesRepository;

@RestController

@RequestMapping("/api/garage-services")
public class GarageServicesController {

    @Autowired
    private GarageServicesRepository garageServicesRepository;

    @GetMapping("/services")
    public List<GarageServices> getGarageServices() {
        List<GarageServices> services = garageServicesRepository.findAll();
        services.sort(Comparator.comparing(GarageServices::getName));
        return services;
    }
}