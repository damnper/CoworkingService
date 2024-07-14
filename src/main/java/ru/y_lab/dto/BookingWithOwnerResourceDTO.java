package ru.y_lab.dto;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BookingWithOwnerResourceDTO is a Data Transfer Object that represents a booking along with user and resource details.
 *
 * @param userId the unique identifier of the user who made the booking
 * @param resourceId the unique identifier of the resource being booked
 * @param bookingId the unique identifier of the booking
 * @param ownerName the username of the user who made the booking
 * @param resourceName the resourceName of the resource being booked
 * @param resourceType the type of the resource being booked
 * @param date the date of the booking
 * @param startTime the start time of the booking
 * @param endTime the end time of the booking
 */
public record BookingWithOwnerResourceDTO(
        @Schema(name = "userId", description = "The unique identifier of the user", example = "1")
        Long userId,
        @Schema(name = "resourceId", description = "The unique identifier of the resource", example = "1")
        Long resourceId,
        @Schema(name = "bookingId", description = "The unique identifier of the booking", example = "1")
        Long bookingId,
        @Schema(name = "ownerName", description = "The owner name of the booking", example = "Username")
        String ownerName,
        @Schema(name = "resourceName", description = "The resource name of booking", example = "Resource name")
        String resourceName,
        @Schema(name = "resourceType", description = "The type of the resource", example = "CONFERENCE_ROOM")
        String resourceType,
        @Schema(name = "date", description = "Date", example = "2024-07-20")
        String date,
        @Schema(name = "startTime", description = "Start time", example = "10:00")
        String startTime,
        @Schema(name = "endTime", description = "End time", example = "12:00")
        String endTime) {
}
