package ru.y_lab.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.AuthenticateException;
import ru.y_lab.exception.AuthorizationException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static ru.y_lab.enums.RoleType.ADMIN;

/**
 * Utility class for handling authentication and authorization.
 */
@Component
@SessionScope
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserRepository userRepository;

    /**
     * Authenticates the user based on login request.
     *
     * @param loginRequest the login request containing username and password
     * @return the authenticated User object
     * @throws UserNotFoundException if the user is not found with the provided credentials
     * @throws IllegalArgumentException if the provided password is incorrect
     */
    public User login(LoginRequestDTO loginRequest) {
        User user = userRepository.getUserByUsername(loginRequest.username())
                .orElseThrow(() -> new UserNotFoundException("User not found with the provided credentials: " + loginRequest.username()));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException("Invalid login request. Please check your credentials.");
        }

        return user;
    }

    /**
     * Authenticates the user and checks their role if required.
     *
     * @param request the HTTP request containing session information
     * @param requiredRole the required role for authorization (can be null if no role check is needed)
     * @return the authenticated UserDTO object
     * @throws SecurityException if the user is not authenticated or not authorized
     */
    public UserDTO authenticate(HttpServletRequest request, String requiredRole) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            throw new AuthenticateException("You are not logged in. Please log in to access this resource.");
        }

        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");

        // Allow access if the current user is an admin
        if (currentUser.role().equals(ADMIN.name())) {
            return currentUser;
        }

        if (requiredRole != null && !requiredRole.equals(currentUser.role())) {
            throw new AuthorizationException("You do not have the necessary permissions to access this resource.");
        }

        return currentUser;
    }

    /**
     * Checks if the current user is authorized to perform an action.
     *
     * @param currentUser the current authenticated user
     * @param targetUserId the ID of the user to be affected by the action
     * @throws SecurityException if the user is not authorized to perform the action
     */
    public void authorize(UserDTO currentUser, Long targetUserId) {
        if (!currentUser.role().equals("ADMIN") && !currentUser.id().equals(targetUserId)) {
            throw new AuthorizationException("You are not authorized to perform this action.");
        }
    }
}

