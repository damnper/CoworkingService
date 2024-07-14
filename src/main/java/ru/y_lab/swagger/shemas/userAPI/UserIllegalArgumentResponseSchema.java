package ru.y_lab.swagger.shemas.userAPI;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Illegal argument for user's responses schema")
public class UserIllegalArgumentResponseSchema {

    @Schema(name = "status", description = "HTTP status code", example = "400")
    private int status;

    @Schema(name = "message", description = "Error message", example = "Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.")
    private String message;

    @Schema(name = "timestamp", description = "Timestamp of the error occurrence", example = "1623949380000")
    private long timestamp;
}
