package com.example.E_voting.service;

import com.example.E_voting.model.User;

public interface UserService {
    User authenticate(String username, String password);
}
