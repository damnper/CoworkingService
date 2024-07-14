package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LoginRequestDTO is a Data Transfer Object for logging in a user.
 *
 * @param username the username of the user
 * @param password the password of the user
 */
public record LoginRequestDTO(
        @Schema(name = "username", description = "The username of the user", example = "Username")
        String username,
        @Schema(name = "password", description = "The password of the user", example = "Password")
        String password) { }