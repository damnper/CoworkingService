package ru.y_lab.util;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;

/**
 * Utility class for handling authentication.
 */
public class AuthenticationUtil {
    private final UserRepository userRepository;

    /**
     * Constructs an AuthenticationUtil object with the specified UserRepository.
     * @param userRepository the repository containing user data
     */
    public AuthenticationUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user by username and password.
     * @param username the username of the user
     * @param password the password of the user
     * @return true if authentication is successful, false otherwise
     * @throws UserNotFoundException if the user with the specified username is not found
     */
    public boolean authenticate(String username, String password) throws UserNotFoundException {
        User user = userRepository.getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}

