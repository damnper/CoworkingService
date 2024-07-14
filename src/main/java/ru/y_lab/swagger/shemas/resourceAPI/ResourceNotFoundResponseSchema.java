package ru.y_lab.swagger.shemas.resourceAPI;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "Resource not found response schema")
public class ResourceNotFoundResponseSchema {

    @Schema(name = "status", description = "HTTP status code", example = "404")
    private int status;

    @ArraySchema(schema = @Schema(description = "Error messages"),
            arraySchema = @Schema(description = "List of possible error messages", example = "[\"The requested resource could not be found. Please check the ID and try again.\", \"The owner of this resource could not be found. Please try again later.\", \"No resources found in the system.\", \"The owner of one or more resources could not be found. Please try again later.\", \"The resource you are trying to update could not be found. Please check the ID and try again.\", \"The resource you are trying to delete could not be found. Please check the ID and try again.\"]"))
    private List<String> messages;

    @Schema(name = "timestamp", description = "Timestamp of the error occurrence", example = "1623949380000")
    private long timestamp;
}
