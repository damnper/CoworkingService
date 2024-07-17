package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ResourceDTO is a Data Transfer Object that represents a resource.
 *
 * @param resourceId the unique identifier of the resource
 * @param ownerId the unique identifier of the user who owns the resource
 * @param resourceName the resourceName of the resource
 * @param resourceType the resourceType of the resource
 */
public record ResourceDTO(

        @Schema(name = "resourceId", description = "The unique identifier of the resource", example = "1")
        Long resourceId,

        @NotNull(message = "Owner ID cannot be null")
        @Schema(name = "ownerId", description = "The unique identifier of the owner (user)", example = "1")
        Long ownerId,

        @NotBlank(message = "Resource resourceName cannot be blank")
        @Schema(name = "resourceName", description = "The resource resourceName of booking", example = "Resource resourceName")
        @Size(min = 3, max = 100, message = "Resource resourceName must be between 3 and 100 characters")
        @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Resource resourceName can only contain letters (both Latin and Cyrillic) and digits, and spaces")
        String resourceName,

        @NotBlank(message = "Resource resourceType cannot be blank")
        @Schema(name = "resourceType", description = "The resourceType of the resource", example = "CONFERENCE_ROOM")
        String resourceType) {}
