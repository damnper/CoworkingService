package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * BookingDTO is a Data Transfer Object that represents a booking.
 *
 * @param bookingId the unique identifier of the booking
 * @param ownerId the unique identifier of the user who made the booking
 * @param resourceId the unique identifier of the resource being booked
 * @param startTime the start time of the booking
 * @param endTime the end time of the booking
 */
public record BookingDTO(

        @NotNull(message = "User ID cannot be null")
        @Schema(name = "ownerId", description = "The unique identifier of the user", example = "1")
        Long ownerId,

        @NotNull(message = "Resource ID cannot be null")
        @Schema(name = "resourceId", description = "The unique identifier of the resource", example = "1")
        Long resourceId,

        @NotNull(message = "Booking ID cannot be null")
        @Schema(name = "id", description = "The unique identifier of the booking", example = "1")
        Long bookingId,

        @NotNull(message = "Start time cannot be null")
        @Schema(name = "startTime", description = "Start time in milliseconds", example = "1721487600000")
        @Pattern(regexp = "^\\p{N}+$", message = "Start time must be a valid number in milliseconds")
        String startTime,

        @NotNull(message = "End time cannot be null")
        @Schema(name = "endTime", description = "End time in milliseconds", example = "1721487600000")
        @Pattern(regexp = "^\\p{N}+$", message = "End time must be a valid number in milliseconds")
        String endTime) { }