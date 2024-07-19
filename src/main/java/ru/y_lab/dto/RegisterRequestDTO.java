package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RegisterRequestDTO is a Data Transfer Object for registering a new user.
 *
 * @param username the username of the new user
 * @param password the password of the new user
 */
public record RegisterRequestDTO(
        @Schema(name = "username", description = "The username of the new user", example = "Username")
        String username,
        @Schema(name = "password", description = "The password of the new user", example = "password123")
        String password) { }
