package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UpdateUserRequestDTO is a Data Transfer Object for updating user details.
 *
 * @param username the new username of the user
 * @param password the new password of the user
 */
public record UpdateUserRequestDTO(
        @Schema(name = "username", description = "The new username of the user", example = "Newusername")
        String username,
        @Schema(name = "password", description = "The new password of the user", example = "newpassword123")
        String password) { }
