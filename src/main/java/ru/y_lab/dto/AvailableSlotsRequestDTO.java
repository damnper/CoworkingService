package ru.y_lab.dto;

/**
 * AvailableSlotsRequestDTO is a Data Transfer Object for requesting available slots.
 *
 * @param resourceId the unique identifier of the resource for which available slots are requested
 * @param date the date for which available slots are requested (epoch time in milliseconds)
 */
public record AvailableSlotsRequestDTO(Long resourceId, Long date) { }
