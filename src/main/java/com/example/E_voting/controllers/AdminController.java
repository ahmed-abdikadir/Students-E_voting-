package com.example.E_voting.controllers;

import com.example.E_voting.service.ElectionService;
import com.example.E_voting.service.AuditService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private AuditService auditService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        // Role-based security check
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        // Add elections with their vote counts to the model
        model.addAttribute("elections", electionService.getAllElections());
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
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            var election = electionService.createElection(name);
            auditService.logAction(username, "CREATE_ELECTION", "Election", election.getId(), "Created election: " + name);
            redirectAttributes.addFlashAttribute("successMessage", "Election '" + name + "' created successfully.");
        } catch (Exception e) {
            auditService.logActionWithError(username, "CREATE_ELECTION", "Election", 0L, "Election name: " + name, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating election: " + e.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @PostMapping("/elections/open/{id}")
    public String openElection(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            electionService.openElection(id);
            auditService.logAction(username, "OPEN_ELECTION", "Election", id, "Opened election");
            redirectAttributes.addFlashAttribute("successMessage", "Election opened successfully.");
        } catch (IllegalStateException e) {
            auditService.logActionWithError(username, "OPEN_ELECTION", "Election", id, "Election ID: " + id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @PostMapping("/elections/close/{id}")
    public String closeElection(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            electionService.closeElection(id);
            auditService.logAction(username, "CLOSE_ELECTION", "Election", id, "Closed election");
            redirectAttributes.addFlashAttribute("successMessage", "Election closed successfully.");
        } catch (Exception e) {
            auditService.logActionWithError(username, "CLOSE_ELECTION", "Election", id, "Election ID: " + id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error closing election: " + e.getMessage());
        }
        return "redirect:/admin/elections";
    }
    
    @DeleteMapping("/elections/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteElection(@PathVariable Long id, HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            String username = (String) session.getAttribute("username");
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            
            electionService.deleteElection(id);
            auditService.logAction(username, "DELETE_ELECTION", "Election", id, "Deleted election");
            return ResponseEntity.ok().body("Election deleted successfully");
        } catch (IllegalArgumentException e) {
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_ELECTION", "Election", id, "Election ID: " + id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_ELECTION", "Election", id, "Election ID: " + id, e.getMessage());
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
    public String showManageCandidatesPage(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        model.addAttribute("candidates", electionService.getAllCandidates());
        model.addAttribute("elections", electionService.getAllElections());
        return "manage-candidates";
    }

    @PostMapping("/candidates")
    public String createCandidate(@RequestParam String name, @RequestParam Long electionId, 
                                   RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        
        try {
            var candidate = electionService.createCandidate(name, electionId);
            auditService.logAction(username, "CREATE_CANDIDATE", "Candidate", candidate.getId(), 
                "Created candidate: " + name + " for election ID: " + electionId);
            redirectAttributes.addFlashAttribute("successMessage", "Candidate '" + name + "' added successfully.");
        } catch (IllegalArgumentException e) {
            auditService.logActionWithError(username, "CREATE_CANDIDATE", "Candidate", 0L, 
                "Name: " + name + ", Election ID: " + electionId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            auditService.logActionWithError(username, "CREATE_CANDIDATE", "Candidate", 0L,
                "Name: " + name + ", Election ID: " + electionId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating candidate: " + e.getMessage());
        }
        
        return "redirect:/admin/candidates";
    }

    @DeleteMapping("/candidates/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id, HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            String username = (String) session.getAttribute("username");
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            
            electionService.deleteCandidate(id);
            auditService.logAction(username, "DELETE_CANDIDATE", "Candidate", id, "Deleted candidate");
            return ResponseEntity.ok().body("Candidate deleted successfully");
        } catch (IllegalArgumentException e) {
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_CANDIDATE", "Candidate", id, 
                "Candidate ID: " + id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_CANDIDATE", "Candidate", id,
                "Candidate ID: " + id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting candidate: " + e.getMessage());
        }
    }

    @GetMapping("/students")
    public String showManageStudentsPage(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        model.addAttribute("students", electionService.getAllUsers());
        return "manage-students";
    }

    @PostMapping("/students/import")
    public String importStudents(@RequestParam("csvFile") org.springframework.web.multipart.MultipartFile file,
                                  RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a CSV file to import.");
                return "redirect:/admin/students";
            }
            
            Map<String, String> results = electionService.importStudentsFromCSVWithResult(file.getInputStream());
            
            int success = Integer.parseInt(results.get("success"));
            int failure = Integer.parseInt(results.get("failure"));
            String errors = results.get("errors");
            
            String message = String.format("Import completed: %d successful, %d failed", success, failure);
            auditService.logAction(username, "IMPORT_STUDENTS", "User", 0L, 
                "Imported " + success + " students from CSV");
            redirectAttributes.addFlashAttribute("successMessage", message);
            
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorDetails", errors);
            }
            
        } catch (Exception e) {
            auditService.logActionWithError(username, "IMPORT_STUDENTS", "User", 0L, 
                "CSV file: " + file.getOriginalFilename(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error importing CSV: " + e.getMessage());
        }
        
        return "redirect:/admin/students";
    }

    @GetMapping("/election/{electionId}/live-results")
    @ResponseBody
    public ResponseEntity<java.util.Map<String, Object>> getLiveResults(@PathVariable Long electionId, HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            Map<String, Long> results = electionService.getElectionResults(electionId);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("results", results);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
