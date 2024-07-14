package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UpdateBookingRequestDTO is a Data Transfer Object for updating booking details.
 *
 * @param startTime the new start time of the booking (epoch time in milliseconds)
 * @param endTime the new end time of the booking (epoch time in milliseconds)
 */
public record UpdateBookingRequestDTO(
        @Schema(name = "startTime", description = "Start time in milliseconds", example = "1721487600000")
        Long startTime,
        @Schema(name = "endTime", description = "End time in milliseconds", example = "1721487600000")
        Long endTime) { }
