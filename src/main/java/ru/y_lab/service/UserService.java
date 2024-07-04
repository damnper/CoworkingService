package ru.y_lab.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;

import java.util.List;

/**
 * The UserService interface defines methods for managing users.
 */
public interface UserService {

    /**
     * Registers a new user.
     *
     * @param userJson JSON representation of the user to be registered
     * @return the registered user as a UserDTO
     * @throws JsonProcessingException if there is an error processing the JSON
     */
    UserDTO registerUser(String userJson) throws JsonProcessingException;

    /**
     * Logs in an existing user.
     *
     * @param loginJson JSON representation of the login request
     * @return the authenticated user as a UserDTO
     * @throws UserNotFoundException if the user with the specified credentials is not found
     * @throws JsonProcessingException if there is an error processing the JSON
     */
    UserDTO loginUser(String loginJson) throws UserNotFoundException, JsonProcessingException;

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the current user as a UserDTO
     */
    UserDTO getCurrentUser();

    /**
     * Retrieves a list of all registered users.
     *
     * @return a list of all registered users as UserDTOs
     */
    List<UserDTO> viewAllUsers();

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the specified ID as a UserDTO
     */
    UserDTO getUserById(Long userId);

    /**
     * Updates the information of an existing user.
     *
     * @param userJson JSON representation of the user with updated information
     * @param currentUser the current authenticated user
     * @return the updated user as a UserDTO
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws JsonProcessingException if there is an error processing the JSON
     */
    UserDTO updateUser(String userJson, UserDTO currentUser) throws UserNotFoundException, JsonProcessingException;

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId the ID of the user to delete
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    void deleteUser(Long userId) throws UserNotFoundException;
}
