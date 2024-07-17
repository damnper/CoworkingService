package ru.y_lab.service.impl;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.y_lab.repo.UserRepo;
import ru.y_lab.service.SessionService;
import ru.y_lab.service.UserService;
import ru.y_lab.util.AuthenticationUtil;

import java.util.List;

import static ru.y_lab.enums.RoleType.ADMIN;
import static ru.y_lab.enums.RoleType.USER;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
@Loggable
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final AuthenticationUtil authUtil;
    private final SessionService sessionService;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return the registered user as a UserDTO
     */
    @Override
    public UserDTO registerUser(RegisterRequestDTO request) {
        User user = User.builder()
                .username(request.username())
                .password(request.password())
                .role("USER")
                .build();

        User registeredUser = userRepo.save(user);
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
        UserDTO authenticatedUser = userMapper.toDTO(authUtil.login(request));
        sessionService.createSession(authenticatedUser, httpRequest);
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
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found. No user exists with the specified ID."));
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

        List<User> users = userRepo.findAllUsers();
        if (users.isEmpty()) throw new UserNotFoundException("No users found in the system.");

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

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found. No user exists with the specified ID."));
        User updatedUser = userRepo.updateUser(request.username(), request.password(), user.getRole(), user.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found. No user exists with the specified ID."));

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

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found. No user exists with the specified ID."));

        userRepo.deleteById(userId);
        sessionService.shutdownSession(httpRequest);
    }
}