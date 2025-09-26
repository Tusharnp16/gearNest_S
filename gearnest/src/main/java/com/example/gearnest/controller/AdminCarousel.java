package com.example.gearnest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.CarouselImage;
import com.example.gearnest.repository.CarouselImageRepository;
import com.example.gearnest.services.FileStorageService;

@Controller
@RequestMapping("/admin/carousel")
public class AdminCarousel {

    private static final String CAROUSEL_UPLOAD_DIR = "carousel-images";

    @Autowired
    private CarouselImageRepository carouselImageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String carouselDisplayPage(Model model) {
        List<CarouselImage> carouselImages = carouselImageRepository.findAll();
        model.addAttribute("carouselImages", carouselImages);
        model.addAttribute("title", " | Carousel");
        model.addAttribute("activePage", "carousel");
        return "admin/carousel";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("carouselImageFile") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            // FIX: Validate the file type to ensure it's an image
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()
                    || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                redirectAttributes.addFlashAttribute("error",
                        "Invalid file format. Only JPG, JPEG, PNG, and GIF files are allowed.");
                return "redirect:/admin/carousel";
            }

            String filename = fileStorageService.storeFile(file, CAROUSEL_UPLOAD_DIR);
            CarouselImage carouselImage = new CarouselImage();
            carouselImage.setFilename(filename);
            carouselImageRepository.save(carouselImage);
            redirectAttributes.addFlashAttribute("success", "Image uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/admin/carousel";
    }

    @GetMapping("/delete/{id}")
    public String deleteImage(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<CarouselImage> optionalImage = carouselImageRepository.findById(id);
            if (optionalImage.isPresent()) {
                CarouselImage image = optionalImage.get();
                fileStorageService.deleteFile(image.getFilename(), CAROUSEL_UPLOAD_DIR);
                carouselImageRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Image deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Image not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "An error occurred while deleting the image: " + e.getMessage());
        }
        return "redirect:/admin/carousel";
    }
}
