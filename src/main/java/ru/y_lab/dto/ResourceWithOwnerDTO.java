package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ResourceWithOwnerDTO is a Data Transfer Object that represents a resource along with its owner details.
 *
 * @param id the unique identifier of the resource
 * @param resourceName the resourceName of the resource
 * @param resourceType the type of the resource
 * @param ownerId the unique identifier of the user who owns the resource
 * @param ownerName the username of the user who owns the resource
 */
public record ResourceWithOwnerDTO(
        @Schema(name = "id", description = "The unique identifier of the resource", example = "1")
        Long id,
        @Schema(name = "resourceName", description = "The resource name of booking", example = "Resource name")
        String resourceName,
        @Schema(name = "resourceType", description = "The type of the resource", example = "CONFERENCE_ROOM")
        String resourceType,
        @Schema(name = "ownerId", description = "The unique identifier of the owner (user)", example = "1")
        Long ownerId,
        @Schema(name = "ownerName", description = "The username of the owner (user)", example = "Username")
        String ownerName) { }
