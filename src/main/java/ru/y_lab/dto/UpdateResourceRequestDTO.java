package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UpdateResourceRequestDTO is a Data Transfer Object for updating resource details.
 *
 * @param name the new resourceName of the resource
 */
public record UpdateResourceRequestDTO(
        @Schema(name = "name", description = "The resource name of booking", example = "Resource name")
        String name) { }
