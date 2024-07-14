package ru.y_lab.swagger.API;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.y_lab.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserControllerAPI {

    ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequestDTO request);

    ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest);

    @Operation(summary = "Get User By ID",
            description = "Retrieves the profile information of a user based on their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user profile." ,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied. User is not authorized to perform this action.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found. No user exists with the specified ID.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId, HttpServletRequest httpRequest);

    ResponseEntity<List<UserDTO>> getAllUsers(HttpServletRequest httpRequest);

    ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequestDTO updateRequest, HttpServletRequest httpRequest);

    ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId, HttpServletRequest httpRequest);
}
