package ru.y_lab.service;

import ru.y_lab.dto.*;

import java.util.List;

/**
 * The UserService interface defines methods for managing users.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return the registered user as a UserDTO
     */
    UserDTO registerUser(RegisterRequestDTO request);

    /**
     * Authenticates a user and logs them in. Stores user information in the session.
     *
     * @param request the login request containing username and password
     * @return the authenticated user as a UserDTO
     */
    TokenResponseDTO loginUser(LoginRequestDTO request);

    /**
     * Retrieves a user by their ID.
     *
     * @return the user as a UserDTO
     */
    UserDTO getUserById(String token);

    /**
     * Retrieves all users in the system. Only accessible by admin users.
     *
     * @return a list of all users as UserDTOs
     */
    List<UserDTO> getAllUsers(String token);

    /**
     * Updates an existing user. Only accessible by the user themselves or an admin.
     *
     * @param request the update request containing updated user details
     * @return the updated user as a UserDTO
     */
    UserDTO updateUser(String token, UpdateUserRequestDTO request);

    /**
     * Deletes a user by their ID.
     */
    void deleteUser(String token);
}
