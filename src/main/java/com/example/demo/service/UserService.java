package com.example.demo.service;

import com.example.demo.dto.request.user.AuthResponse;
import com.example.demo.dto.request.user.LoginRequest;
import com.example.demo.dto.request.user.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    UserDetails loadUserByUsername(String username);

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
