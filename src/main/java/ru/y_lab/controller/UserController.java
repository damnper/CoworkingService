package ru.y_lab.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.*;
import ru.y_lab.service.UserService;
import ru.y_lab.swagger.API.UserControllerAPI;

import java.util.List;

/**
 * Controller for managing users.
 * This class handles HTTP requests for creating, retrieving, updating, and deleting users.
 */
@Tag(name = "User API", description = "Operations about users")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController implements UserControllerAPI {

    private final UserService userService;

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user details
     * @return a {@link ResponseEntity} containing the registered user as a {@link UserDTO} with HTTP status CREATED
     */
    @Override
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO request) {
        UserDTO userDTO = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    /**
     * Authenticates a user and logs them in.
     *
     * @param loginRequest the login request containing username and password
     * @return a {@link ResponseEntity} containing the authenticated user as a {@link UserDTO} with HTTP status OK
     */
    @Override
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        TokenResponseDTO tokenResponseDTO = userService.loginUser(loginRequest);
        return ResponseEntity.ok(tokenResponseDTO);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param token the authentication token of the user making the request
     * @return a {@link ResponseEntity} containing the user as a {@link UserDTO} with HTTP status OK
     */
    @Override
    @GetMapping
    public ResponseEntity<UserDTO> getUserById(@RequestHeader("Authorization") String token) {
        UserDTO userDTO = userService.getUserById(token);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Retrieves all users in the system. Only accessible by admin users.
     *
     * @param token the authentication token of the admin user making the request
     * @return a {@link ResponseEntity} containing a list of all users as {@link UserDTO} with HTTP status OK
     */
    @Override
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        List<UserDTO> users = userService.getAllUsers(token);
        return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user. Only accessible by the user themselves or an admin.
     *
     * @param token the authentication token of the user making the request
     * @param updateRequest the update request containing updated user details
     * @return a {@link ResponseEntity} containing the updated user as a {@link UserDTO} with HTTP status OK
     */
    @Override
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization") String token,
                                              @Valid @RequestBody UpdateUserRequestDTO updateRequest) {
        UserDTO userDTO = userService.updateUser(token, updateRequest);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param token the authentication token of the user making the request
     * @return a {@link ResponseEntity} with HTTP status NO_CONTENT
     */
    @Override
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token) {
        userService.deleteUser(token);
        return ResponseEntity.noContent().build();
    }
}