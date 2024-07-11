package ru.y_lab.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomUserMapper;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;

/**
 * Utility class for handling authentication and authorization.
 */
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserRepository userRepository = new UserRepository();
    private final CustomUserMapper userMapper = new CustomUserMapper();

    /**
     * Authenticates the user.
     *
     * @param loginRequest the LoginRequestDTO object
     * @return the authenticated UserDTO object
     * @throws UserNotFoundException if authentication fails
     */
    public UserDTO authenticateUser(LoginRequestDTO loginRequest) throws UserNotFoundException {
        if (authenticate(loginRequest.username(), loginRequest.password())) {
            User currentUser = userRepository.getUserByUsername(loginRequest.username());
            return userMapper.toDTO(currentUser);
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    /**
     * Authenticates the user and checks their role if required.
     *
     * @param req the HttpServletRequest object
     * @param requiredRole the required role for authorization (can be null if no role check is needed)
     * @return the authenticated UserDTO or null if authentication/authorization fails
     */
    public UserDTO authenticateAndAuthorize(HttpServletRequest req, String requiredRole) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            throw new SecurityException("User is not authenticated");
        }

        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");

        if (requiredRole != null && !requiredRole.equals(currentUser.role())) {
            throw new SecurityException("Access denied");
        }

        return currentUser;
    }

    /**
     * Checks if the current user is authorized to perform an action.
     * <p>
     * The user must either have the "ADMIN" role or be the owner of the resource identified by `userIdFromPath`.
     * If the user is not authorized, this method throws a SecurityException.
     *
     * @param currentUser   the current authenticated user
     * @param userIdFromPath the ID of the user to be affected by the action
     * @return true if the user is authorized to perform the action
     * @throws SecurityException if the user is not authorized to perform the action
     */
    public boolean isUserAuthorizedToAction(UserDTO currentUser, Long userIdFromPath) {
        if (!currentUser.role().equals("ADMIN") && !currentUser.id().equals(userIdFromPath)) {
            throw new SecurityException("Access denied");
        }
        return true;
    }

    /**
     * Authenticates a user by username and password.
     * @param username the username of the user
     * @param password the password of the user
     * @return true if authentication is successful, false otherwise
     */
    @SneakyThrows
    private boolean authenticate(String username, String password) {
        User user;
        try {
            user = userRepository.getUserByUsername(username);

        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("User not found");
        }
        return user != null && user.getPassword().equals(password);
    }

}

