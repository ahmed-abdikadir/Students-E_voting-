package com.example.E_voting.controllers;

import com.example.E_voting.model.User;
import com.example.E_voting.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showRegistrationForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String username, @RequestParam String password, HttpSession session) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(User.Role.STUDENT); // Default role for new users
        userService.save(user);

        // Log the user in by setting session attributes
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().name());

        return "redirect:/student/dashboard";
    }
}
