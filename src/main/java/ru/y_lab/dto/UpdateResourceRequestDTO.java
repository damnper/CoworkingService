package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * UpdateResourceRequestDTO is a Data Transfer Object for updating resource details.
 *
 * @param resourceName the new resourceName of the resource
 */
public record UpdateResourceRequestDTO(

        @NotBlank(message = "Resource resourceName cannot be blank")
        @Schema(name = "resourceName", description = "The resource resourceName of booking", example = "Resource resourceName")
        @Size(min = 3, max = 100, message = "Resource resourceName must be between 3 and 100 characters")
        @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Resource resourceName can only contain letters (both Latin and Cyrillic) and digits, and spaces")
        String resourceName) {}
