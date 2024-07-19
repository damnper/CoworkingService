package ru.y_lab.swagger.API;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.y_lab.dto.*;
import ru.y_lab.swagger.shemas.AccessDeniedResponseSchema;
import ru.y_lab.swagger.shemas.ForbiddenResponseSchema;
import ru.y_lab.swagger.shemas.userAPI.UserIllegalArgumentResponseSchema;
import ru.y_lab.swagger.shemas.userAPI.UserNotFoundResponseSchema;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserControllerAPI {

    @Operation(summary = "Register a new user",
            description = "Registers a new user with the given details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserIllegalArgumentResponseSchema.class)))
    })
    ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequestDTO request);

    @Operation(summary = "Login user",
            description = "Authenticates a user and starts a session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserIllegalArgumentResponseSchema.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class)))
    })
    ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest);

    @Operation(summary = "Get User By ID",
            description = "Retrieves the profile information of a user based on their ID.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user profile.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "User not found. No user exists with the specified ID.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserNotFoundResponseSchema.class)))
    })
    ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId, HttpServletRequest httpRequest);

    @Operation(summary = "Get all users",
            description = "Retrieves all users in the system. Only accessible by admin users.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all users.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "User not found. No user exists with the specified ID.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserNotFoundResponseSchema.class)))
    })
    ResponseEntity<List<UserDTO>> getAllUsers(HttpServletRequest httpRequest);

    @Operation(summary = "Update user",
            description = "Updates an existing user. Only accessible by the user themselves or an admin.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserIllegalArgumentResponseSchema.class))),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "User not found. No user exists with the specified ID.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserNotFoundResponseSchema.class)))
    })
    ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequestDTO updateRequest, HttpServletRequest httpRequest);

    @Operation(summary = "Delete user",
            description = "Deletes a user by their ID. Only accessible by the user themselves or an admin.",
            security = @SecurityRequirement(name = "sessionAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully."),
            @ApiResponse(responseCode = "401", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessDeniedResponseSchema.class))),
            @ApiResponse(responseCode = "403", description = "You do not have the necessary permissions to access this resource.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForbiddenResponseSchema.class))),
            @ApiResponse(responseCode = "404", description = "User not found. No user exists with the specified ID.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserNotFoundResponseSchema.class)))
    })
    ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId, HttpServletRequest httpRequest);
}
