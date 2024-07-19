package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AvailableSlotDTO is a Data Transfer Object that represents an available slot.
 *
 * @param slotNumber the slot number
 * @param slotStart the start time of the slot
 * @param slotEnd the end time of the slot
 */
public record AvailableSlotDTO(
        @Schema(name = "slotNumber", description = "The slot number", example = "1")
        Integer slotNumber,
        @Schema(name = "slotStart", description = "The start time", example = "10:00")
        String slotStart,
        @Schema(name = "slotEnd", description = "The end time", example = "12:00")
        String slotEnd) { }
