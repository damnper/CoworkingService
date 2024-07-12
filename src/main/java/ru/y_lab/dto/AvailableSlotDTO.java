package ru.y_lab.dto;

/**
 * AvailableSlotDTO is a Data Transfer Object that represents an available slot.
 *
 * @param slotNumber the slot number
 * @param slotStart the start time of the slot
 * @param slotEnd the end time of the slot
 */
public record AvailableSlotDTO(Integer slotNumber, String slotStart, String slotEnd) { }
