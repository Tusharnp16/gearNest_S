package com.example.gearnest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.gearnest.model.Cities;
import com.example.gearnest.model.States;
import com.example.gearnest.model.User;
import com.example.gearnest.repository.CityRepository;
import com.example.gearnest.repository.StateRepository;
import com.example.gearnest.repository.UserRepository;
import com.example.gearnest.services.FileStorageService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class userProfile {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/profile")
    public String user_profile(Model model, HttpSession session) {
        Object userObject = session.getAttribute("loggedInUser");
        if (userObject instanceof User) {
            model.addAttribute("user", (User) userObject);
        }
        return "myprofile";
    }

    /**
     * This method handles the form submission for updating the user profile.
     *
     * @param session            The current HTTP session.
     * @param profilePictureFile The uploaded profile picture file.
     * @param firstName          The user's updated first name.
     * @param lastName           The user's updated last name.
     * @param phone              The user's updated phone number.
     * @param address            The user's updated address.
     * @param stateId            The ID of the selected state.
     * @param cityId             The ID of the selected city.
     * @return A ResponseEntity indicating success or failure.
     */
    @PostMapping("/profile/update")
    public ResponseEntity<String> updateProfile(
            HttpSession session,
            @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("state") Long stateId,
            @RequestParam("city") Long cityId) {

        try {
            Object userObject = session.getAttribute("loggedInUser");
            if (!(userObject instanceof User)) {
                return ResponseEntity.status(403).body("User not logged in.");
            }

            User currentUser = (User) userObject;
            // Re-fetch from DB to ensure we have the latest entity
            User userToUpdate = userRepository.findById(currentUser.getId()).orElse(null);
            if (userToUpdate == null) {
                return ResponseEntity.status(404).body("User not found.");
            }

            // Update user details
            userToUpdate.setFirstName(firstName);
            userToUpdate.setLastName(lastName);
            userToUpdate.setPhone(phone);
            userToUpdate.setAddress(address);

            // Find and set state and city
            States state = stateRepository.findById(stateId).orElse(null);
            Cities city = cityRepository.findById(cityId).orElse(null);
            userToUpdate.setState(state);
            userToUpdate.setCity(city);

            // Handle file upload
            if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
                // Get the old filename before updating
                String oldFileName = userToUpdate.getProfilePic();
                String directory = "user-profiles";

                // FIX 1: Provide the directory argument to storeFile()
                String newFileName = fileStorageService.storeFile(profilePictureFile, directory);
                userToUpdate.setProfilePic(newFileName);

                // FIX 2: Pass both the filename and directory to deleteFile()
                if (oldFileName != null) {
                    fileStorageService.deleteFile(oldFileName, directory);
                }
            }

            // Save the updated user and refresh the session object
            User updatedUser = userRepository.save(userToUpdate);
            session.setAttribute("loggedInUser", updatedUser);

            return ResponseEntity.ok("Profile updated successfully!");

        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while updating the profile.");
        }
    }
}