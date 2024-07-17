package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AvailableSlotDTO is a Data Transfer Object that represents an available slot.
 *
 * @param slotNumber the slot number
 * @param slotStart the start time of the slot
 * @param slotEnd the end time of the slot
 */
public record AvailableSlotDTO(

        @NotNull(message = "Slot number cannot be null")
        @Schema(name = "slotNumber", description = "The slot number", example = "1")
        Integer slotNumber,

        @NotBlank(message = "Slot start time cannot be blank")
        @Schema(name = "slotStart", description = "The start time", example = "10:00")
        String slotStart,

        @NotBlank(message = "Slot end time cannot be blank")
        @Schema(name = "slotEnd", description = "The end time", example = "12:00")
        String slotEnd) { }
