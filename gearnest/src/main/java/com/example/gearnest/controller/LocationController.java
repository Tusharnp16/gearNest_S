package com.example.gearnest.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gearnest.model.Cities;
import com.example.gearnest.model.States;
import com.example.gearnest.repository.CityRepository;
import com.example.gearnest.repository.StateRepository;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private StateRepository stateRepo;

    @Autowired
    private CityRepository cityRepo;

    @GetMapping("/states")
    public List<States> getStates() {
        List<States> states = stateRepo.findAll();
        states.sort(Comparator.comparing(States::getName));
        return states;
    }

    @GetMapping("/cities/{stateId}")
    public List<Cities> getCities(@PathVariable Long stateId) {
         List<Cities> cities = cityRepo.findByStateId(stateId);
    cities.sort(Comparator.comparing(Cities::getName));
    return cities;
    }
}