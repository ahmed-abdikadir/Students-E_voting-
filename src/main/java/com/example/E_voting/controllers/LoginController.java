package com.example.E_voting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.E_voting.model.User;
import com.example.E_voting.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/login"})
    public String showLoginPage() {
        return "login"; // This returns login.html from src/main/resources/templates/
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, 
                        @RequestParam("password") String password, 
                        Model model, HttpSession session) {
        User user = userService.authenticate(username, password);

        if (user != null) {
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().name());
            return "redirect:/dashboard"; // Redirect to a dashboard page on successful login
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login"; // Return to login page with an error message
        }
    }
}
