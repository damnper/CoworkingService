package ru.y_lab.dto;

/**
 * AddBookingRequestDTO is a Data Transfer Object for adding a new booking.
 *
 * @param resourceId the unique identifier of the resource to be booked
 * @param startTime the start time of the booking (epoch time in milliseconds)
 * @param endTime the end time of the booking (epoch time in milliseconds)
 */
public record AddBookingRequestDTO(Long resourceId, Long startTime, Long endTime) {}
