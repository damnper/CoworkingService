package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AddBookingRequestDTO is a Data Transfer Object for adding a new booking.
 *
 * @param resourceId the unique identifier of the resource to be booked
 * @param startTime the start time of the booking (epoch time in milliseconds)
 * @param endTime the end time of the booking (epoch time in milliseconds)
 */
public record AddBookingRequestDTO(
        @Schema(name = "resourceId", description = "The unique identifier of resource", example = "1")
        Long resourceId,
        @Schema(name = "startTime", description = "Start time in milliseconds", example = "1721487600000")
        Long startTime,
        @Schema(name = "endTime", description = "End time in milliseconds", example = "1721487600000")
        Long endTime) {}
