package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.y_lab.validation.ValidResourceType;

/**
 * UpdateResourceRequestDTO is a Data Transfer Object for updating resource details.
 *
 * @param resourceName the new resourceName of the resource
 * @param resourceType the resourceType of the resource being booked
 */
public record UpdateResourceRequestDTO(

        @NotBlank(message = "Resource resourceName cannot be blank")
        @Schema(name = "resourceName", description = "The resource resourceName of booking", example = "Resource resourceName")
        @Size(min = 3, max = 100, message = "Resource resourceName must be between 3 and 100 characters")
        @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Resource resourceName can only contain letters (both Latin and Cyrillic) and digits, and spaces")
        String resourceName,

        @NotBlank(message = "Resource resourceType cannot be blank")
        @Schema(name = "resourceType", description = "The resourceType of the resource", example = "CONFERENCE_ROOM")
        @ValidResourceType
        String resourceType) {}
