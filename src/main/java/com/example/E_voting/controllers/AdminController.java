package com.example.E_voting.controllers;

import com.example.E_voting.service.ElectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ElectionService electionService;

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
    public String showManageElectionsPage(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        model.addAttribute("elections", electionService.getAllElections());
        return "manage-elections";
    }

    @PostMapping("/elections/create")
    public String createElection(@RequestParam String name, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        electionService.createElection(name);
        redirectAttributes.addFlashAttribute("successMessage", "Election '" + name + "' created successfully.");
        return "redirect:/admin/elections";
    }

    @PostMapping("/elections/open/{id}")
    public String openElection(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            electionService.openElection(id);
            redirectAttributes.addFlashAttribute("successMessage", "Election opened successfully.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @PostMapping("/elections/close/{id}")
    public String closeElection(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            electionService.closeElection(id);
            redirectAttributes.addFlashAttribute("successMessage", "Election closed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error closing election: " + e.getMessage());
        }
        return "redirect:/admin/elections";
    }
    
    @DeleteMapping("/elections/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteElection(@PathVariable Long id, HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            
            electionService.deleteElection(id);
            return ResponseEntity.ok().body("Election deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting election: " + e.getMessage());
        }
    }
    
    // Keep the old endpoint for backward compatibility
    @DeleteMapping("/elections/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteElectionOld(@PathVariable Long id, HttpSession session) {
        return deleteElection(id, session);
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
