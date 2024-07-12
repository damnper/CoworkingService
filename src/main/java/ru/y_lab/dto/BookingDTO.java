package ru.y_lab.dto;

/**
 * BookingDTO is a Data Transfer Object that represents a booking.
 *
 * @param id the unique identifier of the booking
 * @param userId the unique identifier of the user who made the booking
 * @param resourceId the unique identifier of the resource being booked
 * @param startTime the start time of the booking
 * @param endTime the end time of the booking
 */
public record BookingDTO(Long id, Long userId, Long resourceId, String startTime, String endTime) { }