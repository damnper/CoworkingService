package ru.y_lab.swagger.shemas.userAPI;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * UserNotFoundResponse is a Data Transfer Object for 404 error responses.
 */
@Data
@Schema(name = "User not found response schema")
public class UserNotFoundResponseSchema {

    @Schema(name = "status", description = "HTTP status code", example = "404")
    private int status;

    @ArraySchema(schema = @Schema(description = "Error messages"),
            arraySchema = @Schema(description = "List of possible error messages", example = "[\"User not found. No user exists with the specified ID.\", \"No users found in the system.\", \"No resources found in the system.\", \"The owner of this resource could not be found. Please try again later.\"]"))
    private String message;

    @Schema(name = "timestamp", description = "Timestamp of the error occurrence", example = "1623949380000")
    private long timestamp;
}
