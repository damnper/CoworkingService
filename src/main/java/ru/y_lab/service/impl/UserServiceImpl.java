package ru.y_lab.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import ru.y_lab.util.ValidationUtil;

import java.util.List;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = new UserRepository();
    private final AuthenticationUtil authUtil = new AuthenticationUtil(userRepository);
    private static User currentUser = null;
    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final CustomUserMapper userMapper = new CustomUserMapper();

    /**
     * Registers a new user by deserializing the provided JSON input.
     * Validates the username and password format before registering the user.
     *
     * @param registerRequestJSON JSON representation of the user to be registered
     * @return the registered user as a UserDTO
     */
    @Override
    public UserDTO registerUser(String registerRequestJSON) throws JsonProcessingException {
        RegisterRequestDTO request = objectMapper.readValue(registerRequestJSON, RegisterRequestDTO.class);

        if (!ValidationUtil.validateUsername(request.getUsername())) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (!ValidationUtil.validatePassword(request.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role("USER")
                .build();

        User registeredUser = userRepository.addUser(user);
        return userMapper.toDTO(registeredUser);
    }

    /**
     * Logs in a user by authenticating with the provided username and password.
     * Sets the currentUser static field upon successful authentication.
     *
     * @param loginRequestJSON JSON representation of the login request
     * @return the authenticated user as a UserDTO
     */
    @Override
    public UserDTO loginUser(String loginRequestJSON) throws UserNotFoundException, JsonProcessingException {
        LoginRequestDTO loginRequest = objectMapper.readValue(loginRequestJSON, LoginRequestDTO.class);

        if (authUtil.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
            currentUser = userRepository.getUserByUsername(loginRequest.getUsername());
            return userMapper.toDTO(currentUser);
        } else {
            throw new UserNotFoundException("Invalid credentials");
        }
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a list of all registered users as UserDTOs
     */
    @Override
    public List<UserDTO> viewAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return users.stream().map(userMapper::toDTO).toList();
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the specified ID as a UserDTO
     */
    @Override
    @SneakyThrows
    public UserDTO getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        return userMapper.toDTO(user);
    }

    /**
     * Updates the information of an existing user.
     *
     * @param updateRequestJSON JSON representation of the user with updated information
     * @param currentUser the current authenticated user
     * @return the updated user as a UserDTO
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws JsonProcessingException if there is an error processing the JSON
     */
    @Override
    public UserDTO updateUser(String updateRequestJSON, UserDTO currentUser) throws UserNotFoundException, JsonProcessingException {
        UpdateRequestDTO request = objectMapper.readValue(updateRequestJSON, UpdateRequestDTO.class);
        User user = userRepository.getUserById(currentUser.getId());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return userMapper.toDTO(userRepository.updateUser(user));
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId the ID of the user to delete
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        userRepository.deleteUser(userId);
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the current user as a UserDTO, or null if no user is currently logged in
     */
    @Override
    public UserDTO getCurrentUser() {
        return currentUser != null ? userMapper.toDTO(currentUser) : null;
    }

}