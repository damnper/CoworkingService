package ru.y_lab.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;

/**
 * Utility class for handling authentication.
 */
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserRepository userRepository;

    /**
     * Authenticates a user by username and password.
     * @param username the username of the user
     * @param password the password of the user
     * @return true if authentication is successful, false otherwise
     */
    @SneakyThrows
    public boolean authenticate(String username, String password) {
        User user = userRepository.getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
}

