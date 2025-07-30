package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gearnest.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class AdminUserListController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("title", "-Users");

        model.addAttribute("activePage", "users");
        model.addAttribute("userList", userRepository.findAll());
        System.out.println(userRepository.findAll());
        return "admin/user-list";
    }
}
