package ru.y_lab.swagger.shemas.resourceAPI;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Illegal argument for resource's responses schema")
public class ResourceIllegalArgumentResponseSchema {

    @Schema(name = "status", description = "HTTP status code", example = "400")
    private int status;

    @Schema(name = "message", description = "Error message", example = "Invalid resourceName. Name must be 1 to 50 characters long and can only contain letters (Latin and Cyrillic).")
    private String message;

    @Schema(name = "timestamp", description = "Timestamp of the error occurrence", example = "1623949380000")
    private long timestamp;
}
