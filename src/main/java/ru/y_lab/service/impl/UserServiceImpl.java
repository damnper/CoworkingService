package ru.y_lab.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomUserMapper;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.UserService;
import ru.y_lab.util.AuthenticationUtil;

import java.io.IOException;
import java.util.List;

import static ru.y_lab.util.RequestUtil.*;
import static ru.y_lab.util.ResponseUtil.*;
import static ru.y_lab.util.ValidationUtil.validateRegisterRequest;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CustomUserMapper userMapper = new CustomUserMapper();
    private final UserRepository userRepository = new UserRepository();
    private final AuthenticationUtil authUtil = new AuthenticationUtil(userRepository, userMapper);
//    private final UserMapper userMapper = UserMapper.INSTANCE;


    /**
     * Registers a new user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    public void registerUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestBody = getRequestBody(req);
            RegisterRequestDTO loginRequest = parseRequest(requestBody, RegisterRequestDTO.class);
            UserDTO userDTO = processRegisterUser(loginRequest);
            sendSuccessResponse(resp, 201, userDTO);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Logs in a user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    public void loginUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestBody = getRequestBody(req);
            LoginRequestDTO loginRequest = parseRequest(requestBody, LoginRequestDTO.class);
            UserDTO userDTO = authUtil.authenticateUser(loginRequest);
            createSession(req, userDTO);
            sendSuccessResponse(resp, 200, userDTO);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (UserNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Retrieves all users.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    public void getAllUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, "ADMIN");
            List<UserDTO> users = userRepository.getAllUsers().stream()
                    .map(userMapper::toDTO)
                    .toList();
            sendSuccessResponse(resp, 200, users);
        } catch (SecurityException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    public void getUserById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, null);
            Long userId = Long.parseLong(req.getParameter("id"));
            User user = userRepository.getUserById(userId);
            UserDTO userDTO = userMapper.toDTO(user);
            sendSuccessResponse(resp, 200, userDTO);
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Updates the information of an existing user.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    public void updateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            Long userIdToUpdate = extractUserIdFromPath(req);
            if (!authUtil.isUserAuthorizedToAction(currentUser, userIdToUpdate)) throw new SecurityException("Access denied");

            String requestBody = getRequestBody(req);
            UpdateRequestDTO loginRequest = parseRequest(requestBody, UpdateRequestDTO.class);
            UserDTO userDTO = processUpdatingUser(currentUser, loginRequest);
            createSession(req, userDTO);
            sendSuccessResponse(resp, 200, userDTO);
        } catch (SecurityException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     */
    @Override
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            Long userIdToDelete = extractUserIdFromPath(req);
            if (!authUtil.isUserAuthorizedToAction(currentUser, userIdToDelete)) throw new SecurityException("Access denied");

            processUserDeletion(userIdToDelete, currentUser, req);
            sendSuccessResponse(resp, 204);
        } catch (SecurityException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Registers a new user by deserializing the provided JSON input.
     * Validates the username and password format before registering the user.
     *
     * @param request JSON representation of the user to be registered
     * @return the registered user as a UserDTO
     */
    private UserDTO processRegisterUser(RegisterRequestDTO request) {
        validateRegisterRequest(request);

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role("USER")
                .build();

        User registeredUser = userRepository.addUser(user);
        return userMapper.toDTO(registeredUser);
    }


    /**
     * Processes the update of an existing user.
     *
     * @param currentUser the current authenticated user
     * @param request     the UpdateRequestDTO containing the updated user information
     * @return the updated user as a UserDTO
     * @throws UserNotFoundException if the user is not found
     */
    private UserDTO processUpdatingUser(UserDTO currentUser, UpdateRequestDTO request) throws UserNotFoundException {
        User user = userRepository.getUserById(currentUser.getId());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return userMapper.toDTO(userRepository.updateUser(user));
    }

    /**
     * Processes the deletion of a user.
     *
     * @param userIdToDelete the ID of the user to delete
     * @param currentUser    the current authenticated user
     * @param req            the HttpServletRequest object
     * @throws UserNotFoundException  if the user is not found
     */
    private void processUserDeletion(Long userIdToDelete, UserDTO currentUser, HttpServletRequest req) throws UserNotFoundException {
        userRepository.deleteUser(userIdToDelete);
        if (currentUser.getId().equals(userIdToDelete)) {
            req.getSession().invalidate();
        }
    }
}