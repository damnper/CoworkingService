package ru.y_lab.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * AddResourceRequestDTO is a Data Transfer Object for adding a new resource.
 *
 * @param resourceName the resourceName of the resource
 */
public record AddResourceRequestDTO(

        @NotBlank(message = "Resource resourceName cannot be blank")
        @Size(min = 3, max = 100, message = "Resource resourceName must be between 3 and 100 characters")
        @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Resource resourceName can only contain letters (both Latin and Cyrillic) and digits, and spaces")
        @Schema(name = "resourceName", description = "Resource's resourceName", example = "Resource resourceName")
        String resourceName) { }
