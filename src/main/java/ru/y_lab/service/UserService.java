package ru.y_lab.service;

import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;
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
     * @param httpRequest the HTTP request to get the session
     * @return the authenticated user as a UserDTO
     */
    UserDTO loginUser(LoginRequestDTO request, HttpServletRequest httpRequest);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request to get the session
     * @return the user as a UserDTO
     */
    UserDTO getUserById(Long userId, HttpServletRequest httpRequest);

    /**
     * Retrieves all users in the system. Only accessible by admin users.
     *
     * @param httpRequest the HTTP request to get the session
     * @return a list of all users as UserDTOs
     */
    List<UserDTO> getAllUsers(HttpServletRequest httpRequest);

    /**
     * Updates an existing user. Only accessible by the user themselves or an admin.
     *
     * @param userId the ID of the user to be updated
     * @param request the update request containing updated user details
     * @param httpRequest the HTTP request to get the session
     * @return the updated user as a UserDTO
     */
    UserDTO updateUser(Long userId, UpdateUserRequestDTO request, HttpServletRequest httpRequest);

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to be deleted
     */
    void deleteUser(Long userId, HttpServletRequest httpRequest);
}
