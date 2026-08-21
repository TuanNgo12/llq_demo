package com.example.demo.service.impl;

import com.example.demo.dto.request.user.UserRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.User.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    @Override
    public User createUser(UserRequest userRequest) throws Exception {
        return null;
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        return "";
    }
}
