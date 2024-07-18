package ru.y_lab.service;

public interface JWTService {

    String extractUserName(String token);

    Long extractUserId(String token);

    String extractUserRole(String token);

    String generateToken(String username, Long userId, String role);

    Boolean isTokenValid(String token, String username);

    boolean hasRole(String token, String role);
}
