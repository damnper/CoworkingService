package ru.y_lab.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.service.UserService;

import java.util.List;

/**
 * UserController handles HTTP requests for managing users.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user details
     * @return ResponseEntity containing the registered user as a UserDTO
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequestDTO request) {
        UserDTO userDTO = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    /**
     * Authenticates a user and logs them in.
     *
     * @param request the login request containing username and password
     * @param httpRequest the HTTP request to get the session for authentication
     * @return ResponseEntity containing the authenticated user as a UserDTO
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        UserDTO userDTO = userService.loginUser(request, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request to get the session for authentication
     * @return ResponseEntity containing the user as a UserDTO
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId, HttpServletRequest httpRequest) {
        UserDTO userDTO = userService.getUserById(userId, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Retrieves all users in the system. Only accessible by admin users.
     *
     * @param httpRequest the HTTP request to get the session for authentication
     * @return ResponseEntity containing a list of all users as UserDTOs
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(HttpServletRequest httpRequest) {
        List<UserDTO> users = userService.getAllUsers(httpRequest);
        return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user. Only accessible by the user themselves or an admin.
     *
     * @param userId the ID of the user to be updated
     * @param updateRequest the update request containing updated user details
     * @param httpRequest the HTTP request to get the session for authentication
     * @return ResponseEntity containing the updated user as a UserDTO
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequestDTO updateRequest, HttpServletRequest httpRequest) {
        UserDTO userDTO = userService.updateUser(userId, updateRequest, httpRequest);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to be deleted
     * @param httpRequest the HTTP request to get the session for authentication
     * @return ResponseEntity with HTTP status NO_CONTENT
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId, HttpServletRequest httpRequest) {
        userService.deleteUser(userId, httpRequest);
        return ResponseEntity.noContent().build();
    }
}