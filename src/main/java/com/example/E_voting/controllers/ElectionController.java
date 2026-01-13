package com.example.E_voting.controllers;

import com.example.E_voting.model.Candidate;
import com.example.E_voting.model.Election;
import com.example.E_voting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @GetMapping("/election/{id}")
    public String showBallot(@PathVariable("id") Long electionId, Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/login";
        }
        
        String studentId = (String) session.getAttribute("username");
        if (electionService.hasVoted(studentId, electionId)) {
            return "already-voted";
        }
        Election election = electionService.getElectionById(electionId);
        List<Candidate> candidates = electionService.getCandidatesByElectionId(electionId);

        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);

        return "ballot";
    }

    @PostMapping("/vote")
    public String submitVote(@RequestParam Long electionId, @RequestParam Long candidateId, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/login";
        }
        
        session.setAttribute("electionId", electionId);
        session.setAttribute("candidateId", candidateId);
        return "redirect:/student/vote/confirm";
    }

    @GetMapping("/vote/confirm")
    public String confirmVote(@RequestParam(required = false) Long candidateId, 
                               @RequestParam(required = false) Long electionId,
                               HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/login";
        }
        
        // If query parameters are provided, use them
        if (candidateId != null && electionId != null) {
            session.setAttribute("electionId", electionId);
            session.setAttribute("candidateId", candidateId);
        }

        Long sessionElectionId = (Long) session.getAttribute("electionId");
        Long sessionCandidateId = (Long) session.getAttribute("candidateId");

        if (sessionElectionId == null || sessionCandidateId == null) {
            return "redirect:/student/dashboard";
        }

        model.addAttribute("election", electionService.getElectionById(sessionElectionId));
        model.addAttribute("candidate", electionService.getCandidateById(sessionCandidateId));

        return "confirm-vote";
    }

    @PostMapping("/vote/confirm")
    public String castVote(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/login";
        }
        
        String studentId = (String) session.getAttribute("username");
        Long electionId = (Long) session.getAttribute("electionId");
        Long candidateId = (Long) session.getAttribute("candidateId");

        if (studentId == null || electionId == null || candidateId == null) {
            return "redirect:/login";
        }

        electionService.castVote(studentId, electionId, candidateId);

        session.removeAttribute("electionId");
        session.removeAttribute("candidateId");

        return "redirect:/student/vote/success";
    }

    @GetMapping("/vote/success")
    public String voteSuccess(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            return "redirect:/login";
        }
        
        model.addAttribute("role", role);
        return "vote-success";
    }

    @GetMapping("/vote-success")
    public String voteSuccessAlternate() {
        return "redirect:/student/vote/success";
    }

    @GetMapping("/election/{electionId}/results")
    public String viewResults(@PathVariable Long electionId, HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        Election election = electionService.getElectionById(electionId);
        if (election == null) {
            return "redirect:/dashboard"; // Or an error page
        }

        Map<String, Long> results = electionService.getElectionResults(electionId);
        long totalVotes = results.values().stream().mapToLong(Long::longValue).sum();

        model.addAttribute("election", election);
        model.addAttribute("results", results);
        model.addAttribute("totalVotes", totalVotes);

        return "election-results";
    }
}
