package ru.y_lab.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.y_lab.validation.ValidResourceType;

/**
 * BookingWithOwnerResourceDTO is a Data Transfer Object that represents a booking along with user and resource details.
 *
 * @param ownerId the unique identifier of the user who made the booking
 * @param resourceId the unique identifier of the resource being booked
 * @param bookingId the unique identifier of the booking
 * @param ownerName the username of the user who made the booking
 * @param resourceName the resourceName of the resource being booked
 * @param resourceType the resourceType of the resource being booked
 * @param date the date of the booking
 * @param startTime the start time of the booking
 * @param endTime the end time of the booking
 */
public record BookingWithOwnerResourceDTO(

        @NotNull(message = "Owner ID cannot be null")
        @Schema(name = "ownerId", description = "The unique identifier of the user", example = "1")
        Long ownerId,

        @NotNull(message = "Resource ID cannot be null")
        @Schema(name = "resourceId", description = "The unique identifier of the resource", example = "1")
        Long resourceId,

        @NotNull(message = "Booking ID cannot be null")
        @Schema(name = "bookingId", description = "The unique identifier of the booking", example = "1")
        Long bookingId,

        @NotNull(message = "Owner resourceName cannot be null")
        @Schema(name = "ownerName", description = "The owner resourceName of the booking", example = "Username")
        @Pattern(regexp = "^\\p{L}+$", message = "Owner resourceName can only contain letters (both Latin and Cyrillic)")
        String ownerName,

        @NotBlank(message = "Resource resourceName cannot be blank")
        @Schema(name = "resourceName", description = "The resource resourceName of booking", example = "Resource resourceName")
        @Size(min = 3, max = 100, message = "Resource resourceName must be between 3 and 100 characters")
        @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Resource resourceName can only contain letters (both Latin and Cyrillic), digits, and spaces")
        String resourceName,

        @NotBlank(message = "Resource resourceType cannot be blank")
        @Schema(name = "resourceType", description = "The resourceType of the resource", example = "CONFERENCE_ROOM")
        @ValidResourceType
        String resourceType,

        @Schema(name = "date", description = "Date", example = "2024-07-20")
        @NotBlank(message = "Date cannot be blank")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in the format YYYY-MM-DD")
        String date,

        @NotBlank(message = "Start time cannot be blank")
        @Schema(name = "startTime", description = "Start time", example = "10:00")
        @Pattern(regexp = "\\d{2}:\\d{2}", message = "Start time must be in the format HH:MM")
        String startTime,

        @NotBlank(message = "End time cannot be blank")
        @Schema(name = "endTime", description = "End time", example = "12:00")
        @Pattern(regexp = "\\d{2}:\\d{2}", message = "End time must be in the format HH:MM")
        String endTime) {
}
