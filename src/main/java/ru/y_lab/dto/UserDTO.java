package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
        @NotBlank(message = "Username cannot be blank")
        @Pattern(regexp = "^\\p{L}+$", message = "Username can only contain letters (both Latin and Cyrillic)")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Role cannot be blank")
        @Pattern(regexp = "USER|ADMIN", message = "Role must be either USER or ADMIN")
        @Schema(name = "role", description = "The role of the user (e.g., USER, ADMIN)", example = "USER")
        String role) { }