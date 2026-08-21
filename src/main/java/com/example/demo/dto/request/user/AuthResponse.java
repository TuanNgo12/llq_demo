package com.example.demo.dto.request.user;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username
) {
    public static AuthResponse of(String accessToken, long expiresInMs, String username) {
        return new AuthResponse(accessToken, "Bearer", expiresInMs / 1000, username);
    }
}
