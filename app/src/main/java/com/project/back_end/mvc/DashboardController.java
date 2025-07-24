package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.service.Service;

@Controller
public class DashboardController {

    @Autowired
    private Service service; // Service for validating user tokens and roles

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Validate the token for an admin role
        String error = service.validateToken(token, "admin");
        if (error == null || error.isEmpty()) {
            // Token is valid; show admin dashboard
            return "admin/adminDashboard";
        }
        // Invalid token; redirect to home/login page
        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        // Validate the token for a doctor role
        String error = service.validateToken(token, "doctor");
        if (error == null || error.isEmpty()) {
            // Token is valid; show doctor dashboard
            return "doctor/doctorDashboard";
        }
        // Invalid token; redirect to home/login page
        return "redirect:/";
    }
}
