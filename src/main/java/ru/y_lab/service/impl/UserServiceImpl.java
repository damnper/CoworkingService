package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.*;
import ru.y_lab.enums.RoleType;
import ru.y_lab.exception.AuthorizationException;
import ru.y_lab.exception.InvalidCredentialsException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.UserMapper;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepo;
import ru.y_lab.service.JWTService;
import ru.y_lab.service.UserService;

import java.util.List;

import static ru.y_lab.enums.RoleType.ADMIN;

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
    private final JWTService jwtService;

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

    @Override
    public TokenResponseDTO loginUser(LoginRequestDTO request) {
        User user = userRepo.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.getPassword().equals(request.password())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRole());

        return new TokenResponseDTO(token);

    }

    @Override
    public UserDTO getUserById(String token) {
        Long userId = jwtService.extractUserId(token);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found. No user exists with the specified ID."));
        return userMapper.toDTO(user);
    }

//    @Override
    public List<UserDTO> getAllUsers(String token) {
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) throw new UserNotFoundException("No users found in the system.");

        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public UserDTO updateUser(String token, UpdateUserRequestDTO request) {
        Long userId = jwtService.extractUserId(token);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found. No user exists with the specified ID."));

        user.setUsername(request.username());
        user.setPassword(request.password());

        User updatedUser = userRepo.save(user);

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(String token) {
        Long userId = jwtService.extractUserId(token);

        userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found. No user exists with the specified ID."));
        userRepo.deleteById(userId);
    }
}