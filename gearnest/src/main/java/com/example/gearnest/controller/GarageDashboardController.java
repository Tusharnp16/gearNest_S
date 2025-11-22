package com.example.gearnest.controller;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gearnest.model.Booking;
import com.example.gearnest.model.Garage;
import com.example.gearnest.repository.BookingRepository;
import com.example.gearnest.repository.FeedbackRepository;
import com.example.gearnest.repository.GarageRepository;
import com.example.gearnest.repository.ParticularGarageServiceRepository;

@Controller
@RequestMapping("/garage")
public class GarageDashboardController {

        @Autowired
        private GarageRepository garageRepository;

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private FeedbackRepository feedbackRepository;

        // @Autowired
        // private UserRepository userRepository;

        @Autowired
        private ParticularGarageServiceRepository particularGarageServiceRepository;

        @GetMapping("/dashboard")
        public String viewDashboard(Principal principal, Model model, RedirectAttributes redirectAttributes) {
                if (principal == null) {
                        redirectAttributes.addFlashAttribute("error", "You must be logged in.");
                        return "redirect:/login";
                }

                Garage garage = garageRepository.findByEmail(principal.getName());
                if (garage == null) {
                        redirectAttributes.addFlashAttribute("error", "Garage profile not found.");
                        return "redirect:/logout";
                }

                Long garageId = garage.getId();

                List<Booking> allBookings = bookingRepository.findByGarageId(garageId);
                long totalBookings = allBookings.size();

                long totalCustomers = allBookings.stream()
                                .map(Booking::getUser) // Get the User object from each booking
                                .filter(user -> user != null) // Ensure user is not null
                                .distinct() // Count only unique users
                                .count();

                long bookedCount = allBookings.stream().filter(b -> "Booked".equalsIgnoreCase(b.getStatus())).count();
                long inProgressCount = allBookings.stream().filter(b -> "In Progress".equalsIgnoreCase(b.getStatus()))
                                .count();
                long completedCount = allBookings.stream().filter(b -> "Completed".equalsIgnoreCase(b.getStatus()))
                                .count();
                long cancelledCount = allBookings.stream().filter(b -> "Cancelled".equalsIgnoreCase(b.getStatus()))
                                .count();

                double totalEarnings = allBookings.stream()
                                .filter(b -> "Completed".equalsIgnoreCase(b.getStatus()))
                                .mapToDouble(Booking::getTotalFee)
                                .sum();

                long totalServices = particularGarageServiceRepository.countByGarageId(garageId);

                List<Integer> ratings = feedbackRepository.findRatingsByGarageId(garageId);
                double averageRating = ratings.stream().mapToDouble(r -> r).average().orElse(0.0);

                // Current week Monday → Sunday
                LocalDate today = LocalDate.now();
                LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
                // LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

                List<String> revenueLabels = IntStream.rangeClosed(0, 6)
                                .mapToObj(i -> startOfWeek.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT,
                                                Locale.ENGLISH))
                                .collect(Collectors.toList());

                List<Double> revenueData = IntStream.rangeClosed(0, 6)
                                .mapToObj(i -> {
                                        LocalDate date = startOfWeek.plusDays(i);
                                        return allBookings.stream()
                                                        .filter(b -> "Completed".equalsIgnoreCase(b.getStatus())
                                                                        && b.getBookingDate().toLocalDate()
                                                                                        .isEqual(date))
                                                        .mapToDouble(Booking::getTotalFee)
                                                        .sum();
                                })
                                .collect(Collectors.toList());

                model.addAttribute("title", "Dashboard");
                model.addAttribute("garage", garage);
                model.addAttribute("totalCustomers", totalCustomers);
                model.addAttribute("totalServices", totalServices);
                model.addAttribute("totalBookings", totalBookings);
                model.addAttribute("totalEarnings", totalEarnings);
                model.addAttribute("averageRating", String.format("%.1f", averageRating));
                model.addAttribute("bookingCounts",
                                new long[] { bookedCount, inProgressCount, completedCount, cancelledCount });

                model.addAttribute("revenueData",
                                revenueData.stream().map(String::valueOf).collect(Collectors.joining(",")));
                model.addAttribute("revenueLabels", String.join(",", revenueLabels));
                model.addAttribute("activePage", "dashboard");

                return "garage/garage-dashboard";
        }

        @GetMapping("/dashboard/revenue")
        @ResponseBody
        public Map<String, Double> getGarageRevenueData(@RequestParam String type, Principal principal) {
                Garage garage = garageRepository.findByEmail(principal.getName());
                if (garage == null) {
                        return Map.of(); // return empty if garage not found
                }

                List<Booking> allBookings = bookingRepository.findByGarageId(garage.getId());
                LocalDate now = LocalDate.now();
                Map<String, Double> result = new LinkedHashMap<>();

                switch (type.toLowerCase()) {
                        case "weekly": {
                                // Sunday → Saturday
                                LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                                for (int i = 0; i < 7; i++) {
                                        LocalDate day = startOfWeek.plusDays(i);
                                        String key = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                                        double sum = allBookings.stream()
                                                        .filter(b -> "Completed".equalsIgnoreCase(b.getStatus())
                                                                        && b.getBookingDate() != null
                                                                        && b.getBookingDate().toLocalDate()
                                                                                        .isEqual(day))
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
                                                        .filter(b -> "Completed".equalsIgnoreCase(b.getStatus())
                                                                        && b.getBookingDate() != null
                                                                        && YearMonth.from(b.getBookingDate()
                                                                                        .toLocalDate()).equals(ym))
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
                                                        .filter(b -> "Completed".equalsIgnoreCase(b.getStatus())
                                                                        && b.getBookingDate() != null
                                                                        && b.getBookingDate().getYear() == year)
                                                        .mapToDouble(Booking::getTotalFee)
                                                        .sum();

                                        result.put(key, sum);
                                }
                                break;
                        }
                }
                return result;
        }

}
