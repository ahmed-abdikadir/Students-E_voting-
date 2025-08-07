package com.example.E_voting.controllers;

import org.springframework.stereotype.Controller;
import com.example.E_voting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class dashboardController {

    @Autowired
    private ElectionService electionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session){
        model.addAttribute("elections", electionService.getAllElections());
        return "dashboard";
    }
}
