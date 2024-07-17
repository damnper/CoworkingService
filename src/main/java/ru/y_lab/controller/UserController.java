package ru.y_lab.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.service.UserService;
import ru.y_lab.swagger.API.UserControllerAPI;

import java.util.List;

/**
 * Controller for managing users.
 * This class handles HTTP requests for creating, retrieving, updating, and deleting users.
 */
@Tag(name = "User API", description = "Operations about users")
@RestController
@RequestMapping("/users")
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
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO request) {
        UserDTO userDTO = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    /**
     * Authenticates a user and logs them in.
     *
     * @param loginRequest the login request containing username and password
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing the authenticated user as a {@link UserDTO} with HTTP status OK
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest,
                                             HttpServletRequest httpRequest) {
        UserDTO userDTO = userService.loginUser(loginRequest, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing the user as a {@link UserDTO} with HTTP status OK
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId, HttpServletRequest httpRequest) {
        UserDTO userDTO = userService.getUserById(userId, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Retrieves all users in the system. Only accessible by admin users.
     *
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing a list of all users as {@link UserDTO} with HTTP status OK
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(HttpServletRequest httpRequest) {
        List<UserDTO> users = userService.getAllUsers(httpRequest);
        return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user. Only accessible by the user themselves or an admin.
     *
     * @param userId the ID of the user to be updated
     * @param updateRequest the update request containing updated user details
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing the updated user as a {@link UserDTO} with HTTP status OK
     */
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(Long userId, @Valid @RequestBody UpdateUserRequestDTO updateRequest,
                                              HttpServletRequest httpRequest) {
        UserDTO userDTO = userService.updateUser(userId, updateRequest, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to be deleted
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} with HTTP status NO_CONTENT
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(Long userId, HttpServletRequest httpRequest) {
        userService.deleteUser(userId, httpRequest);
        return ResponseEntity.noContent().build();
    }
}