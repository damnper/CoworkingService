package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * UpdateUserRequestDTO is a Data Transfer Object for updating user details.
 *
 * @param username the new username of the user
 * @param password the new password of the user
 */
public record UpdateUserRequestDTO(

        @NotBlank(message = "Username cannot be blank")
        @Schema(name = "username", description = "The username of the user", example = "Username")
        @Pattern(regexp = "^\\p{L}+$", message = "Username can only contain letters (both Latin and Cyrillic)")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Schema(name = "password", description = "The password of the user", example = "Password")
        @Pattern(regexp = "^[a-zA-Z0-9@#%]{6,20}$", message = "Password must be between 6 and 20 characters and can contain letters, digits, and @#% symbols")
        String password) { }
