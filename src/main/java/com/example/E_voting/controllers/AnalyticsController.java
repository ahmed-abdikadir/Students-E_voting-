package com.example.E_voting.controllers;

import com.example.E_voting.service.ElectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/analytics")
public class AnalyticsController {

    @Autowired
    private ElectionService electionService;

    @GetMapping("/dashboard/{electionId}")
    public String showAnalyticsDashboard(@PathVariable Long electionId, HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        
        var election = electionService.getElectionById(electionId);
        var candidates = electionService.getCandidatesByElectionId(electionId);
        var results = electionService.getElectionResults(electionId);
        
        System.out.println("[ANALYTICS] Dashboard - Election: " + electionId + ", Candidates: " + candidates.size() + ", Results: " + results);
        
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        model.addAttribute("electionId", electionId);
        model.addAttribute("results", results);
        
        return "analytics-dashboard";
    }

    @GetMapping("/api/live-votes/{electionId}")
    @ResponseBody
    public ResponseEntity<?> getLiveVotes(@PathVariable Long electionId, HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            System.out.println("[ANALYTICS] getLiveVotes called - ElectionId: " + electionId + ", Role: " + role);
            
            if (!"ADMIN".equals(role)) {
                System.out.println("[ANALYTICS] Access denied - insufficient role");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            // Get election results
            Map<String, Long> results = electionService.getElectionResults(electionId);
            System.out.println("[ANALYTICS] Election Results: " + results);
            
            // Get candidates
            var candidates = electionService.getCandidatesByElectionId(electionId);
            System.out.println("[ANALYTICS] Candidates count: " + candidates.size());
            
            // Build response with candidate data
            List<Map<String, Object>> chartData = new ArrayList<>();
            long totalVotes = 0;
            
            for (var candidate : candidates) {
                Map<String, Object> data = new HashMap<>();
                long voteCount = results.getOrDefault(candidate.getName(), 0L);
                data.put("name", candidate.getName());
                data.put("votes", voteCount);
                data.put("id", candidate.getId());
                chartData.add(data);
                totalVotes += voteCount;
                System.out.println("[ANALYTICS] Candidate: " + candidate.getName() + ", Votes: " + voteCount);
            }
            
            // Sort by votes descending
            chartData.sort((a, b) -> Long.compare((Long) b.get("votes"), (Long) a.get("votes")));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", chartData);
            response.put("totalVotes", totalVotes);
            response.put("timestamp", System.currentTimeMillis());
            
            System.out.println("[ANALYTICS] Returning response with " + chartData.size() + " candidates, total votes: " + totalVotes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("[ANALYTICS ERROR] Exception in getLiveVotes: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/api/vote-distribution/{electionId}")
    @ResponseBody
    public ResponseEntity<?> getVoteDistribution(@PathVariable Long electionId, HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            Map<String, Long> results = electionService.getElectionResults(electionId);
            var candidates = electionService.getCandidatesByElectionId(electionId);
            
            long totalVotes = results.values().stream().mapToLong(Long::longValue).sum();
            
            // Calculate percentages
            List<Map<String, Object>> distributionData = candidates.stream()
                .map(candidate -> {
                    Map<String, Object> data = new HashMap<>();
                    long votes = results.getOrDefault(candidate.getName(), 0L);
                    double percentage = totalVotes > 0 ? (votes * 100.0 / totalVotes) : 0;
                    data.put("name", candidate.getName());
                    data.put("votes", votes);
                    data.put("percentage", Math.round(percentage * 100.0) / 100.0);
                    return data;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("votes"), (Long) a.get("votes")))
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", distributionData);
            response.put("totalVotes", totalVotes);
            response.put("candidateCount", candidates.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/api/election-summary/{electionId}")
    @ResponseBody
    public ResponseEntity<?> getElectionSummary(@PathVariable Long electionId, HttpSession session) {
        try {
            String role = (String) session.getAttribute("role");
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            var election = electionService.getElectionById(electionId);
            var candidates = electionService.getCandidatesByElectionId(electionId);
            Map<String, Long> results = electionService.getElectionResults(electionId);
            
            long totalVotes = results.values().stream().mapToLong(Long::longValue).sum();
            long maxVotes = results.values().stream().mapToLong(Long::longValue).max().orElse(0L);
            
            // Find leading candidate
            String leadingCandidate = results.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("electionName", election != null ? election.getName() : "N/A");
            response.put("electionStatus", election != null ? election.getStatus() : "N/A");
            response.put("totalVotes", totalVotes);
            response.put("candidateCount", candidates.size());
            response.put("leadingCandidate", leadingCandidate);
            response.put("maxVotes", maxVotes);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
