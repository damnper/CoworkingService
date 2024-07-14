package ru.y_lab.swagger.shemas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AccessDeniedResponse is a Data Transfer Object for 401 error responses.
 */
@Schema(name = "Access denied response schema")
@Data
public class AccessDeniedResponseSchema {

    @Schema(name = "status", description = "HTTP status code", example = "401")
    private int status;

    @Schema(name = "message", description = "Error message", example = "You are not logged in. Please log in to access this resource.")
    private String message;

    @Schema(name = "timestamp", description = "Timestamp of the error occurrence", example = "1623949380000")
    private long timestamp;
}
