package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * UpdateBookingRequestDTO is a Data Transfer Object for updating booking details.
 *
 * @param startTime the new start time of the booking (epoch time in milliseconds)
 * @param endTime the new end time of the booking (epoch time in milliseconds)
 */
public record UpdateBookingRequestDTO(

        @NotNull(message = "Start time cannot be null")
        @Schema(name = "startTime", description = "Start time in milliseconds", example = "1721487600000")
        @Pattern(regexp = "^\\p{N}+$", message = "Start time must be a valid number in milliseconds")
        Long startTime,

        @NotNull(message = "End time cannot be null")
        @Schema(name = "endTime", description = "End time in milliseconds", example = "1721487600000")
        @Pattern(regexp = "^\\p{N}+$", message = "End time must be a valid number in milliseconds")
        String endTime) { }
