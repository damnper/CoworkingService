package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.UserMapper;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.UserService;
import ru.y_lab.util.AuthenticationUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static ru.y_lab.enums.RoleType.ADMIN;
import static ru.y_lab.enums.RoleType.USER;
import static ru.y_lab.util.ValidationUtil.*;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
@Loggable
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthenticationUtil authUtil;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return the registered user as a UserDTO
     */
    @Override
    public UserDTO registerUser(RegisterRequestDTO request) {
        validateRegisterRequest(request);

        User user = User.builder()
                .username(request.username())
                .password(request.password())
                .role("USER")
                .build();

        User registeredUser = userRepository.addUser(user);
        return userMapper.toDTO(registeredUser);
    }

    /**
     * Authenticates a user and logs them in. Stores user information in the session.
     *
     * @param request the login request containing username and password
     * @param httpRequest the HTTP request to get the session
     * @return the authenticated user as a UserDTO
     */
    @Override
    public UserDTO loginUser(LoginRequestDTO request, HttpServletRequest httpRequest) {
        validateLoginRequest(request);
        UserDTO authenticatedUser = userMapper.toDTO(authUtil.login(request));
        createSession(authenticatedUser, httpRequest);
        return authenticatedUser;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @return the user as a UserDTO
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    @Override
    public UserDTO getUserById(Long userId, HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, USER.name());
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + userId));
        return userMapper.toDTO(user);
    }

    /**
     * Retrieves all users in the system. Only accessible by admin users.
     *
     * @param httpRequest the HTTP request to get the session
     * @return a list of all users as UserDTOs
     * @throws SecurityException if the current user is not an admin
     */
    @Override
    public List<UserDTO> getAllUsers(HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, ADMIN.name());

        List<User> users = userRepository.getAllUsers()
                .orElseThrow(() -> new UserNotFoundException("No users found in the system."));
        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    /**
     * Updates an existing user. Only accessible by the user themselves or an admin.
     *
     * @param userId the ID of the user to be updated
     * @param request the update request containing updated user details
     * @param httpRequest the HTTP request to get the session
     * @return the updated user as a UserDTO
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws SecurityException if the current user is not authorized to update the user
     */
    @Override
    public UserDTO updateUser(Long userId, UpdateUserRequestDTO request, HttpServletRequest httpRequest) {
        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        authUtil.authorize(currentUser, userId);
        validateUpdateUserRequest(request);

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + userId));
        user.setUsername(request.username());
        user.setPassword(request.password());

        User updatedUser = userRepository.updateUser(user)
                .orElseThrow(() -> new UserNotFoundException("User not found after update by ID: " + userId));
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Deletes a user by their ID. Only accessible by the user themselves or an admin.
     *
     * @param userId the ID of the user to be deleted
     * @param httpRequest the HTTP request to get the session
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws SecurityException if the current user is not authorized to delete the user
     */
    @Override
    public void deleteUser(Long userId, HttpServletRequest httpRequest) {
        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        authUtil.authorize(currentUser, userId);

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for delete by ID: " + userId));

        userRepository.deleteUser(userId);
        shutdownSession(httpRequest);
    }

    /**
     * Creates a session and stores the authenticated user in it.
     *
     * @param user the authenticated user
     * @param request the HTTP request to get the session
     */
    private void createSession(UserDTO user, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);
        session.setMaxInactiveInterval(30 * 60); // Session timeout in seconds
    }

    /**
     * Invalidates the current user session.
     *
     * <p>This method retrieves the current {@link HttpSession} from the provided {@link HttpServletRequest}
     * and invalidates it, effectively logging out the user. If there is no current session,
     * the method does nothing.</p>
     *
     * @param httpRequest the {@link HttpServletRequest} from which to retrieve the session
     * @throws IllegalStateException if the session has already been invalidated
     */
    private static void shutdownSession(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}