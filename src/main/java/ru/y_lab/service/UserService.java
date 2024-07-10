package ru.y_lab.service;

import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;

import java.util.List;

/**
 * The UserService interface defines methods for managing users.
 */
public interface UserService {

    UserDTO registerUser(RegisterRequestDTO request);

    UserDTO loginUser(LoginRequestDTO request) throws UserNotFoundException;

    UserDTO getUserById(Long userId) throws UserNotFoundException;

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long userId, UpdateUserRequestDTO request) throws UserNotFoundException;

    void deleteUser(Long userId) throws UserNotFoundException;

//
//    /**
//     * Registers a new user.
//     *
//     * @param req  the HttpServletRequest object
//     * @param resp the HttpServletResponse object
//     */
//    void registerUser(HttpServletRequest req, HttpServletResponse resp) throws IOException;
//
//    /**
//     * Logs in a user.
//     *
//     * @param req  the HttpServletRequest object
//     * @param resp the HttpServletResponse object
//     */
//    void loginUser(HttpServletRequest req, HttpServletResponse resp) throws IOException;
//
//    /**
//     * Retrieves a user by their ID.
//     *
//     * @param req  the HttpServletRequest object
//     * @param resp the HttpServletResponse object
//     */
//    void getUserById(HttpServletRequest req, HttpServletResponse resp) throws IOException;
//
//    /**
//     * Retrieves all users.
//     *
//     * @param req  the HttpServletRequest object
//     * @param resp the HttpServletResponse object
//     */
//    void getAllUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException;
//
//    /**
//     * Updates the information of an existing user.
//     *
//     * @param req  the HttpServletRequest object
//     * @param resp the HttpServletResponse object
//     */
//    void updateUser(HttpServletRequest req, HttpServletResponse resp) throws UserNotFoundException, IOException;
//
//    /**
//     * Deletes a user by their unique identifier.
//     *
//     * @param req  the HttpServletRequest object
//     * @param resp the HttpServletResponse object
//     */
//    void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws UserNotFoundException, IOException;
}
