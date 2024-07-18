package ru.y_lab.swagger.shemas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ForbiddenResponse is a Data Transfer Object for 403 error responses.
 */
@Data
@Schema(name = "Forbidden response schema")
public class ForbiddenResponseSchema {

    @Schema(name = "status", description = "HTTP status code", example = "403")
    private int status;

    @Schema(name = "message", description = "Error message", example = "Access denied. Only admin users can access this resource.")
    private String message;

    @Schema(name = "timestamp", description = "Timestamp of the error occurrence", example = "1623949380000")
    private long timestamp;
}
