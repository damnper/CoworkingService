package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BookingDTO is a Data Transfer Object that represents a booking.
 *
 * @param id the unique identifier of the booking
 * @param userId the unique identifier of the user who made the booking
 * @param resourceId the unique identifier of the resource being booked
 * @param startTime the start time of the booking
 * @param endTime the end time of the booking
 */
public record BookingDTO(
        @Schema(name = "id", description = "The unique identifier of the booking", example = "1")
        Long id,
        @Schema(name = "userId", description = "The unique identifier of the user", example = "1")
        Long userId,
        @Schema(name = "resourceId", description = "The unique identifier of the resource", example = "1")
        Long resourceId,
        @Schema(name = "startTime", description = "Start time in milliseconds", example = "1721487600000")
        String startTime,
        @Schema(name = "endTime", description = "End time in milliseconds", example = "1721487600000")
        String endTime) { }