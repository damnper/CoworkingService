package ru.y_lab.util;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;

/**
 * Utility class for handling authentication.
 */
public class AuthenticationUtil {
    private final UserRepository userRepository;

    public AuthenticationUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user by username and password.
     * @param username the username of the user
     * @param password the password of the user
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate(String username, String password) throws UserNotFoundException {
        User user = userRepository.getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}
