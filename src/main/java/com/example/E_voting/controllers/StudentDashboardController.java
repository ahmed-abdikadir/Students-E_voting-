package com.example.E_voting.controllers;

import org.springframework.stereotype.Controller;
import com.example.E_voting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {

    @Autowired
    private ElectionService electionService;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, HttpSession session){
        // Role-based security check
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/login";
        }
        
        model.addAttribute("elections", electionService.getAllElections());
        return "dashboard";
    }
}
