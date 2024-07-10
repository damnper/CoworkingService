package ru.y_lab.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomUserMapper;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.UserService;
import ru.y_lab.util.AuthenticationUtil;

import java.util.List;

import static ru.y_lab.util.ValidationUtil.*;

/**
 * The UserServiceImpl class provides an implementation of the UserService interface.
 * It interacts with the UserRepository to perform CRUD operations.
 */
@Loggable
@Service
public class UserServiceImpl implements UserService {

    private final CustomUserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthenticationUtil authUtil;

    @Autowired
    public UserServiceImpl(CustomUserMapper userMapper, UserRepository userRepository, AuthenticationUtil authUtil) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }


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

    @Override
    public UserDTO loginUser(LoginRequestDTO request) throws UserNotFoundException {
        validateLoginRequest(request);
        return authUtil.authenticateUser(request);
    }

    @Override
    public UserDTO getUserById(Long userId) throws UserNotFoundException {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + userId));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public UserDTO updateUser(Long userId, UpdateUserRequestDTO request) throws UserNotFoundException {
        validateUpdateUserRequest(request);

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + userId));
        user.setUsername(request.username());
        user.setPassword(request.password());

        User updatedUser = userRepository.updateUser(user)
                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + userId));
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        userRepository.deleteUser(userId);
    }
}