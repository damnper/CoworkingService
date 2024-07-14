package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AvailableSlotsRequestDTO is a Data Transfer Object for requesting available slots.
 *
 * @param resourceId the unique identifier of the resource for which available slots are requested
 * @param date the date for which available slots are requested (epoch time in milliseconds)
 */
public record AvailableSlotsRequestDTO(
        @Schema(name = "resourceId", description = "The unique identifier of the resource", example = "1")
        Long resourceId,
        @Schema(name = "date", description = "The date when available slots are requested (epoch time in milliseconds)", example = "1721487600000")
        Long date) { }
