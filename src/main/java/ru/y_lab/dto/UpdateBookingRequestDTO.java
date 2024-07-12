package ru.y_lab.dto;

/**
 * UpdateBookingRequestDTO is a Data Transfer Object for updating booking details.
 *
 * @param startTime the new start time of the booking (epoch time in milliseconds)
 * @param endTime the new end time of the booking (epoch time in milliseconds)
 */
public record UpdateBookingRequestDTO(Long startTime, Long endTime) { }
