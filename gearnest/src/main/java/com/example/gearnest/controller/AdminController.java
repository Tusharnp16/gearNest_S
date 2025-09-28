package com.example.gearnest.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.GarageProfileRepository;
import com.example.gearnest.repository.GarageServicesRepository;
import com.example.gearnest.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

        @Autowired
        private GarageProfileRepository garageRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private GarageServicesRepository garageServicesRepository;

        @GetMapping("/admin/dashboard")
        public String Dashboard(Model model) {
                model.addAttribute("title", " ");
                model.addAttribute("activePage", "dashboard");

                model.addAttribute("userCount", userRepository.count());
                model.addAttribute("garageCount", garageRepository.count());
                model.addAttribute("bookingCount", bookingRepository.count());
                model.addAttribute("serviceCount", garageServicesRepository.count());

                List<Booking> allBookings = bookingRepository.findAll();
                double totalRevenue = allBookings.stream()
                                .mapToDouble(Booking::getTotalFee)
                                .sum();

                model.addAttribute("totalRevenue", String.format("%.2f", totalRevenue));
                return "admin/admin-dashboard";
        }

        // ---------- REVENUE ----------
        @GetMapping("/admin/chart/revenue")
        @ResponseBody
        public Map<String, Double> getRevenueData(@RequestParam String type) {
                List<Booking> allBookings = bookingRepository.findAll();
                LocalDate now = LocalDate.now();
                Map<String, Double> result = new LinkedHashMap<>();

                switch (type) {
                        case "weekly": {
                                LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                                for (int i = 0; i < 7; i++) {
                                        LocalDate day = startOfWeek.plusDays(i);
                                        String key = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                                        double sum = allBookings.stream()
                                                        .filter(b -> b.getBookingDate() != null &&
                                                                        b.getBookingDate().toLocalDate().isEqual(day))
                                                        .mapToDouble(Booking::getTotalFee)
                                                        .sum();
                                        result.put(key, sum);
                                }
                                break;
                        }
                        case "monthly": {
                                int year = now.getYear();
                                for (int m = 1; m <= 12; m++) {
                                        YearMonth ym = YearMonth.of(year, m);
                                        String key = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                                        double sum = allBookings.stream()
                                                        .filter(b -> b.getBookingDate() != null &&
                                                                        YearMonth.from(b.getBookingDate()).equals(ym))
                                                        .mapToDouble(Booking::getTotalFee)
                                                        .sum();
                                        result.put(key, sum);
                                }
                                break;
                        }
                        case "yearly":
                        default: {
                                int currentYear = now.getYear();
                                for (int i = 4; i >= 0; i--) {
                                        int year = currentYear - i;
                                        String key = String.valueOf(year);
                                        double sum = allBookings.stream()
                                                        .filter(b -> b.getBookingDate() != null &&
                                                                        b.getBookingDate().getYear() == year)
                                                        .mapToDouble(Booking::getTotalFee)
                                                        .sum();
                                        result.put(key, sum);
                                }
                                break;
                        }
                }
                return result;
        }

        // ---------- BOOKINGS ----------
        @GetMapping("/admin/chart/bookings")
        @ResponseBody
        public Map<String, Long> getBookingData(@RequestParam String type) {
                List<Booking> allBookings = bookingRepository.findAll();
                LocalDate now = LocalDate.now();
                Map<String, Long> result = new LinkedHashMap<>();

                switch (type) {
                        case "weekly": {
                                LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                                for (int i = 0; i < 7; i++) {
                                        LocalDate day = startOfWeek.plusDays(i);
                                        String key = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                                        long count = allBookings.stream()
                                                        .filter(b -> b.getBookingDate() != null &&
                                                                        b.getBookingDate().toLocalDate().isEqual(day))
                                                        .count();
                                        result.put(key, count);
                                }
                                break;
                        }
                        case "monthly": {
                                int year = now.getYear();
                                for (int m = 1; m <= 12; m++) {
                                        YearMonth ym = YearMonth.of(year, m);
                                        String key = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                                        long count = allBookings.stream()
                                                        .filter(b -> b.getBookingDate() != null &&
                                                                        YearMonth.from(b.getBookingDate()).equals(ym))
                                                        .count();
                                        result.put(key, count);
                                }
                                break;
                        }
                        case "yearly":
                        default: {
                                int currentYear = now.getYear();
                                for (int i = 4; i >= 0; i--) {
                                        int year = currentYear - i;
                                        String key = String.valueOf(year);
                                        long count = allBookings.stream()
                                                        .filter(b -> b.getBookingDate() != null &&
                                                                        b.getBookingDate().getYear() == year)
                                                        .count();
                                        result.put(key, count);
                                }
                                break;
                        }
                }
                return result;
        }

        @GetMapping("/admin/garages")
        public String viewAllGaragesForAdmin(Model model) {
                List<Garage> garages = garageRepository.findAll();
                model.addAttribute("title", "-Garages");
                model.addAttribute("activePage", "garages");
                model.addAttribute("garages", garages);
                return "admin/admin-garages";
        }

        @PostMapping("/admin/garage/{id}/update-status")
        public String updateGarageStatus(@PathVariable Long id,
                        @RequestParam("status") String status,
                        HttpSession session) {
                Garage garage = garageRepository.findById(id).orElse(null);
                if (garage != null) {
                        garage.setStatus(status);
                        garage.setApproved("Active".equals(status));
                        garageRepository.save(garage);
                        session.setAttribute("success", "Garage status updated.");
                } else {
                        session.setAttribute("error", "Garage not found.");
                }
                return "redirect:/admin/garages";
        }
}
