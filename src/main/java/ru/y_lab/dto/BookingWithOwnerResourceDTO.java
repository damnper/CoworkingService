package ru.y_lab.dto;


/**
 * BookingWithOwnerResourceDTO is a Data Transfer Object that represents a booking along with user and resource details.
 *
 * @param userId the unique identifier of the user who made the booking
 * @param resourceId the unique identifier of the resource being booked
 * @param bookingId the unique identifier of the booking
 * @param ownerName the username of the user who made the booking
 * @param resourceName the name of the resource being booked
 * @param resourceType the type of the resource being booked
 * @param date the date of the booking
 * @param startTime the start time of the booking
 * @param endTime the end time of the booking
 */
public record BookingWithOwnerResourceDTO(
        Long userId,
        Long resourceId,
        Long bookingId,
        String ownerName,
        String resourceName,
        String resourceType,
        String date,
        String startTime,
        String endTime) {
}
