package ru.y_lab.swagger.shemas.bookingAPI;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Illegal argument for booking's responses schema")
public class BookingIllegalArgumentResponseSchema {

    @Schema(name = "status", description = "HTTP status code", example = "400")
    private int status;

    @Schema(name = "message", description = "Error message", example = "Start time must be provided and must be a Long.")
    private String message;

    @Schema(name = "timestamp", description = "Timestamp of the error occurrence", example = "1623949380000")
    private long timestamp;
}
