package com.example.E_voting.service.impl;

import com.example.E_voting.model.User;
import com.example.E_voting.repository.UserRepository;
import com.example.E_voting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        seedUsers();
    }

        private void seedUsers() {
        if (userRepository.count() == 0) {
            // Initialize 3 admins
            for (int i = 1; i <= 3; i++) {
                User admin = new User();
                admin.setUsername("admin" + i);
                admin.setPassword("adminpass" + i);
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);
            }

            // Initialize 5 students
            for (int i = 1; i <= 5; i++) {
                User student = new User();
                student.setUsername("student" + i);
                student.setPassword("studentpass" + i);
                student.setRole(User.Role.STUDENT);
                userRepository.save(student);
            }
        }
    }

    @Override
    public User authenticate(String username, String password) {
        return userRepository.findById(username)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }
}
