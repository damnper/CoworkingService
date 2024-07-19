package ru.y_lab.swagger.API;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.*;
import ru.y_lab.enums.ResourceType;
import ru.y_lab.swagger.shemas.AccessDeniedResponseSchema;
import ru.y_lab.swagger.shemas.ForbiddenResponseSchema;
import ru.y_lab.swagger.shemas.resourceAPI.ResourceIllegalArgumentResponseSchema;
import ru.y_lab.swagger.shemas.resourceAPI.ResourceNotFoundResponseSchema;

import java.util.List;

public interface ResourceControllerAPI {

    @Operation(summary = "Add a new resource",
            description = "Adds a new resource with the given details.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource added successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceIllegalArgumentResponseSchema.class))),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class)))
    })
    ResponseEntity<ResourceDTO> addResource(@RequestHeader("Authorization") String token,
                                            @RequestBody AddResourceRequestDTO addResourceRequest,
                                            @RequestParam ResourceType resourceType);

    @Operation(summary = "Get Resource By ID",
            description = "Retrieves the resource information based on its ID.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the resource information.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceWithOwnerDTO.class))),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found. No resource exists with the specified ID.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
    })
    ResponseEntity<ResourceWithOwnerDTO> getResourceById(@RequestHeader("Authorization") String token,
                                                         @PathVariable("resourceId") Long resourceId);

    @Operation(summary = "Get all resources",
            description = "Retrieves all resources in the system.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all resources.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceWithOwnerDTO.class))),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "No resources found in the system.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
    })
    ResponseEntity<List<ResourceWithOwnerDTO>> getAllResources(@RequestHeader("Authorization") String token);

    @Operation(summary = "Update resource",
            description = "Updates an existing resource.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceIllegalArgumentResponseSchema.class))),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found. No resource exists with the specified ID.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
    })
    ResponseEntity<ResourceDTO> updateResource(@RequestHeader("Authorization") String token,
                                               @PathVariable("resourceId") Long resourceId,
                                               @RequestBody UpdateResourceRequestDTO request,
                                               @RequestParam ResourceType resourceType);

    @Operation(summary = "Delete resource",
            description = "Deletes a resource by its ID.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource deleted successfully."),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found. No resource exists with the specified ID.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResourceNotFoundResponseSchema.class)))
    })
    ResponseEntity<Void> deleteResource(@RequestHeader("Authorization") String token,
                                        @PathVariable("resourceId") Long resourceId);
}
