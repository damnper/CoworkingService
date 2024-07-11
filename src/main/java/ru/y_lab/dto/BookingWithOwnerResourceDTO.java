package ru.y_lab.dto;

public record BookingWithOwnerResourceDTO(
        Long userId,
        Long resourceId,
        Long bookingId,
        String username,
        String resourceName,
        String resourceType,
        String date,
        String startTime,
        String endTime) {
}
