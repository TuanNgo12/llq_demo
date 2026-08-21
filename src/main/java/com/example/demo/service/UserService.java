package com.example.demo.service;

import com.example.demo.dto.request.user.UserRequest;
import com.example.demo.entity.User;

public interface UserService {

    User createUser(UserRequest userRequest) throws Exception;
    String login(String phoneNumber, String password, Long roleId) throws Exception;

}
