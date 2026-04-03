package com.example.E_voting.controllers;

import com.example.E_voting.service.ElectionService;
import com.example.E_voting.service.AuditService;
import com.example.E_voting.service.UserService;
import com.example.E_voting.service.AnnouncementService;
import com.example.E_voting.model.Announcement;
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

    @Autowired
    private UserService userService;

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        // Role-based security check
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        // Add elections with their vote counts to the model
        model.addAttribute("elections", electionService.getAllElections());
        // Add pending application count for dashboard badge
        model.addAttribute("pendingAppCount", electionService.getPendingApplications().size());
        model.addAttribute("announcementCount", announcementService.getActiveAnnouncements().size());

        // Dynamic KPI Stats
        long totalStudents = userService.countStudents();
        long activeElectionsCount = electionService.getAllElections().stream()
            .filter(e -> e.isActive())
            .count();
        long votesCastToday = electionService.getVotesCastSince(java.time.LocalDate.now().atStartOfDay());

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("activeElectionsCount", activeElectionsCount);
        model.addAttribute("votesCastToday", votesCastToday);
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
    public String createElection(@RequestParam String name, 
            @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startTime,
            @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endTime,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            var election = electionService.createElection(name, startTime, endTime);
            auditService.logAction(username, "CREATE_ELECTION", "Election", election.getId(),
                    "Created election: " + name);
            redirectAttributes.addFlashAttribute("successMessage", "Election '" + name + "' created successfully.");
        } catch (Exception e) {
            auditService.logActionWithError(username, "CREATE_ELECTION", "Election", 0L, "Election name: " + name,
                    e.getMessage());
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
            auditService.logActionWithError(username, "OPEN_ELECTION", "Election", id, "Election ID: " + id,
                    e.getMessage());
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
            auditService.logActionWithError(username, "CLOSE_ELECTION", "Election", id, "Election ID: " + id,
                    e.getMessage());
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
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_ELECTION", "Election",
                    id, "Election ID: " + id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_ELECTION", "Election",
                    id, "Election ID: " + id, e.getMessage());
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
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_CANDIDATE", "Candidate",
                    id,
                    "Candidate ID: " + id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            auditService.logActionWithError((String) session.getAttribute("username"), "DELETE_CANDIDATE", "Candidate",
                    id,
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
    public ResponseEntity<java.util.Map<String, Object>> getLiveResults(@PathVariable Long electionId,
            HttpSession session) {
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

    @GetMapping("/profile")
    public String showAdminProfile(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        return "admin-profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password and confirmation do not match.");
            return "redirect:/admin/profile";
        }

        try {
            boolean success = userService.changePassword(username, currentPassword, newPassword);
            if (success) {
                auditService.logAction(username, "CHANGE_PASSWORD", "User", 0L,
                        "Admin changed their password successfully.");
                redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully.");
            } else {
                auditService.logActionWithError(username, "CHANGE_PASSWORD", "User", 0L,
                        "Failed password change attempt", "Invalid current password");
                redirectAttributes.addFlashAttribute("errorMessage", "Incorrect current password.");
            }
        } catch (Exception e) {
            auditService.logActionWithError(username, "CHANGE_PASSWORD", "User", 0L, "Error changing password",
                    e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while changing your password.");
        }

        return "redirect:/admin/profile";
    }

    // ── Applications ─────────────────────────────────────────────────────────

    @GetMapping("/applications")
    public String showApplicationsPage(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        model.addAttribute("applications", electionService.getPendingApplications());
        return "manage-applications";
    }

    @PostMapping("/applications/approve/{id}")
    public String approveApplication(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            var app = electionService.approveApplication(id);
            auditService.logAction(username, "APPROVE_APPLICATION", "CandidateApplication", id,
                    "Approved application for " + app.getUser().getUsername());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application approved. " + app.getUser().getUsername() + " is now a candidate.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/applications";
    }

    @PostMapping("/applications/reject/{id}")
    public String rejectApplication(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            var app = electionService.rejectApplication(id);
            auditService.logAction(username, "REJECT_APPLICATION", "CandidateApplication", id,
                    "Rejected application for " + app.getUser().getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Application rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/applications";
    }

    // ── Announcements ────────────────────────────────────────────────────────

    @GetMapping("/announcements")
    public String showAnnouncementsPage(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        model.addAttribute("announcements", announcementService.getActiveAnnouncements());
        return "manage-announcements";
    }

    @PostMapping("/announcements/create")
    public String createAnnouncement(@RequestParam String title, @RequestParam String content,
            RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            announcementService.saveAnnouncement(new Announcement(title, content));
            auditService.logAction(username, "CREATE_ANNOUNCEMENT", "Announcement", 0L,
                    "Created announcement: " + title);
            redirectAttributes.addFlashAttribute("successMessage", "Announcement published.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/announcements";
    }

    @PostMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String username = (String) session.getAttribute("username");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            announcementService.deleteAnnouncement(id);
            auditService.logAction(username, "DELETE_ANNOUNCEMENT", "Announcement", id, "Deleted announcement");
            redirectAttributes.addFlashAttribute("successMessage", "Announcement deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/announcements";
    }
}
