package com.example.E_voting.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String showAdminDashboard(HttpSession session) {
        // Role-based security check
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        return "admin-dashboard";
    }

    @GetMapping("/elections")
    public String showManageElectionsPage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        return "manage-elections";
    }

    @GetMapping("/candidates")
    public String showManageCandidatesPage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        return "manage-candidates";
    }

    @GetMapping("/students")
    public String showManageStudentsPage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        return "manage-students";
    }
}
