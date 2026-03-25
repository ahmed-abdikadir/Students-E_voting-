package com.example.E_voting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.E_voting.service.ElectionService;
import com.example.E_voting.service.UserService;
import com.example.E_voting.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import com.example.E_voting.service.AnnouncementService;
import com.example.E_voting.model.CandidateApplication;
import com.example.E_voting.model.Vote;
import com.example.E_voting.model.Election;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnouncementService announcementService;

    // ── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, HttpSession session) {
        if (!isStudent(session))
            return "redirect:/login";
            
        String username = (String) session.getAttribute("username");
        List<Election> elections = electionService.getAllElections();
        model.addAttribute("elections", elections);
        
        // Add Announcements
        model.addAttribute("announcements", announcementService.getActiveAnnouncements());
        
        // Track voting progress
        List<Vote> votes = electionService.getVotesByStudentId(username);
        Set<Long> votedElectionIds = votes.stream()
            .map(Vote::getElectionId)
            .collect(Collectors.toSet());
            
        model.addAttribute("votedElectionIds", votedElectionIds);
        model.addAttribute("hasVotedAtAll", !votedElectionIds.isEmpty());
        
        // Candidate Profiles
        Map<Long, List<CandidateApplication>> candidateProfiles = new HashMap<>();
        for (Election e : elections) {
            candidateProfiles.put(e.getId(), electionService.getApprovedApplicationsByElection(e.getId()));
        }
        model.addAttribute("candidateProfiles", candidateProfiles);
        
        return "dashboard";
    }

    // ── Account ───────────────────────────────────────────────────────────────

    @GetMapping("/account")
    public String accountPage(Model model, HttpSession session) {
        if (!isStudent(session) && !isAdmin(session))
            return "redirect:/login";
        String username = (String) session.getAttribute("username");
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("elections", electionService.getAllElections());
        return "account";
    }

    @PostMapping("/account/change-password")
    public String changePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isStudent(session) && !isAdmin(session))
            return "redirect:/login";
        String username = (String) session.getAttribute("username");

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
            return "redirect:/student/account";
        }

        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "New password must be at least 6 characters.");
            return "redirect:/student/account";
        }

        boolean changed = userService.changePassword(username, currentPassword, newPassword);
        if (changed) {
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
        }
        return "redirect:/student/account";
    }

    @GetMapping("/apply")
    public String applyPage(Model model, HttpSession session) {
        if (!isStudent(session))
            return "redirect:/login";
        model.addAttribute("elections", electionService.getAllElections());
        return "apply";
    }

    @PostMapping("/apply")
    public String applyForPosition(@RequestParam Long electionId,
            @RequestParam String motive,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isStudent(session))
            return "redirect:/login";
        String username = (String) session.getAttribute("username");
        try {
            electionService.applyForCandidate(username, electionId, motive);
            redirectAttributes.addFlashAttribute("applySuccess", "Your application has been submitted! An admin will review it shortly.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("applyError", e.getMessage());
        }
        return "redirect:/student/apply";
    }

    // ── Live Votes API (student-accessible) ───────────────────────────────────

    @GetMapping("/api/live-votes/{electionId}")
    @ResponseBody
    public ResponseEntity<?> getStudentLiveVotes(@PathVariable Long electionId, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (role == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authenticated");
        }

        try {
            Map<String, Long> results = electionService.getElectionResults(electionId);
            var candidates = electionService.getCandidatesByElectionId(electionId);

            List<Map<String, Object>> chartData = new ArrayList<>();
            long totalVotes = 0;

            for (var candidate : candidates) {
                Map<String, Object> data = new HashMap<>();
                long votes = results.getOrDefault(candidate.getName(), 0L);
                data.put("name", candidate.getName());
                data.put("votes", votes);
                chartData.add(data);
                totalVotes += votes;
            }

            chartData.sort((a, b) -> Long.compare((Long) b.get("votes"), (Long) a.get("votes")));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", chartData);
            response.put("totalVotes", totalVotes);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isStudent(HttpSession session) {
        return "STUDENT".equals(session.getAttribute("role"));
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }
}
