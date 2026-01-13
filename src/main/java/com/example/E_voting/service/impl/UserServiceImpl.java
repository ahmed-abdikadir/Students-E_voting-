package com.example.E_voting.service.impl;

import com.example.E_voting.model.User;
import com.example.E_voting.repository.UserRepository;
import com.example.E_voting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl() {
    }

    @PostConstruct
    private void seedUsers() {
        if (userRepository.count() == 0) {
            // Initialize 3 admins
            for (int i = 1; i <= 3; i++) {
                User admin = new User();
                admin.setUsername("admin" + i);
                admin.setPassword(passwordEncoder.encode("adminpass" + i));
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);
            }

            // Initialize 5 students
            for (int i = 1; i <= 5; i++) {
                User student = new User();
                student.setUsername("student" + i);
                student.setPassword(passwordEncoder.encode("studentpass" + i));
                student.setRole(User.Role.STUDENT);
                userRepository.save(student);
            }
        }
    }

    @Override
    public User authenticate(String username, String password) {
        if (username == null || password == null || passwordEncoder == null) {
            return null;
        }
        return userRepository.findById(username)
                .filter(user -> user.getPassword() != null && passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }

    @Override
    public User save(User user) {
        // Hash password if it's not already hashed
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
}
