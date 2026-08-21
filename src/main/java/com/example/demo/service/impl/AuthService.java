package com.example.demo.service.impl;

import com.example.demo.dto.request.user.AuthResponse;
import com.example.demo.dto.request.user.LoginRequest;
import com.example.demo.dto.request.user.RegisterRequest;
import com.example.demo.entity.Roles;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtTokenUtils;
import com.example.demo.repository.Roles.RoleRepository;
import com.example.demo.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUserName(req.username())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        Roles userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role ROLE_USER chưa được seed trong DB"));

        User user = User.builder()
                .userName(req.username())
                .email(req.email())
                .passWord(passwordEncoder.encode(req.password()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        String token = jwtTokenUtils.generateToken(user);
        return AuthResponse.of(token, jwtTokenUtils.getExpirationMs(), user.getUsername());
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        // Nếu sai username/password, AuthenticationManager tự ném BadCredentialsException

        UserDetails user = userDetailsService.loadUserByUsername(req.username());
        String token = jwtTokenUtils.generateToken(user);
        return AuthResponse.of(token, jwtTokenUtils.getExpirationMs(), user.getUsername());
    }

}
