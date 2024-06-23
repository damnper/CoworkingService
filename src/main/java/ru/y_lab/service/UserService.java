package ru.y_lab.service;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

/**
 * The UserService interface defines methods for managing users.
 */
public interface UserService {

    /**
     * Registers a new user.
     * @return the registered user
     */
    User registerUser();

    /**
     * Logs in an existing user.
     * @throws UserNotFoundException if the user is not found
     */
    void loginUser() throws UserNotFoundException;

    /**
     * Retrieves the currently logged-in user.
     * @return the current user
     */
    User getCurrentUser();

    /**
     * Displays information about all registered users.
     */
    void viewAllUsers();

    /**
     * Retrieves a user by their unique identifier.
     * @param userId the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws UserNotFoundException if the user with the given ID is not found
     */
    User getUserById(String userId) throws UserNotFoundException;
}
