package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.y_lab.exception.UserNotFoundException;

import java.io.IOException;

/**
 * The UserService interface defines methods for managing users.
 */
public interface UserService {

    /**
     * Registers a new user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    void registerUser(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Logs in a user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    void loginUser(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves all users.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    void getAllUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves a user by their ID.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    void getUserById(HttpServletRequest req, HttpServletResponse resp) throws IOException;


    void updateUser(HttpServletRequest req, HttpServletResponse resp) throws UserNotFoundException, IOException;


    void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws UserNotFoundException, IOException;
}
