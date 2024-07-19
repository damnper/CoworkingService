package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UserDTO is a Data Transfer Object that represents a user in the system.
 * It contains the user's ID, username, and role.
 *
 * @param id       the unique identifier of the user
 * @param username the username of the user
 * @param role     the role of the user (e.g., USER, ADMIN)
 */
public record UserDTO(
        @Schema(name = "id", description = "The unique identifier of the user", example = "1")
        Long id,
        @Schema(name = "username", description = "The username of the user", example = "Username")
        String username,
        @Schema(name = "role", description = "The role of the user (e.g., USER, ADMIN)", example = "USER")
        String role) { }