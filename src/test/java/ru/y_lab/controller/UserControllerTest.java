package ru.y_lab.controller;


import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.AuthenticateException;
import ru.y_lab.exception.AuthorizationException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Register User")
    void registerUser() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("username", "password");
        UserDTO userDTO = new UserDTO(1L, "username", "USER");

        when(userService.registerUser(registerRequest)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.registerUser(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService).registerUser(registerRequest);
    }

    @Test
    @DisplayName("Register User - Invalid Data")
    void registerUserInvalidData() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO("", "password"); // Invalid username

        when(userService.registerUser(registerRequest)).thenThrow(new IllegalArgumentException("Invalid username"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userController.registerUser(registerRequest));

        assertEquals("Invalid username", exception.getMessage());
        verify(userService).registerUser(registerRequest);
    }

    @Test
    @DisplayName("Login User")
    void loginUser() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("username", "password");
        UserDTO userDTO = new UserDTO(1L, "username", "USER");

        when(userService.loginUser(loginRequest, httpRequest)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.loginUser(loginRequest, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService).loginUser(loginRequest, httpRequest);
    }

    @Test
    @DisplayName("Login User - Invalid Credentials")
    void loginUserInvalidCredentials() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("username", "wrongPassword");

        when(userService.loginUser(loginRequest, httpRequest)).thenThrow(new AuthenticateException("Invalid credentials"));

        Exception exception = assertThrows(AuthenticateException.class, () -> userController.loginUser(loginRequest, httpRequest));

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userService).loginUser(loginRequest, httpRequest);
    }

    @Test
    @DisplayName("Get User By ID")
    void getUserById() {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO(1L, "username", "USER");

        when(userService.getUserById(userId, httpRequest)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUserById(userId, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService).getUserById(userId, httpRequest);
    }

    @Test
    @DisplayName("Get User By ID - User Not Found")
    void getUserByIdUserNotFound() {
        Long userId = 1L;

        when(userService.getUserById(userId, httpRequest)).thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userController.getUserById(userId, httpRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userService).getUserById(userId, httpRequest);
    }

    @Test
    @DisplayName("Get All Users")
    void getAllUsers() {
        List<UserDTO> users = List.of(
                new UserDTO(1L, "username1", "USER"),
                new UserDTO(2L, "username2", "USER")
        );

        when(userService.getAllUsers(httpRequest)).thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers(httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService).getAllUsers(httpRequest);
    }

    @Test
    @DisplayName("Get All Users - Unauthorized")
    void getAllUsersUnauthorized() {
        when(userService.getAllUsers(httpRequest)).thenThrow(new AuthorizationException("Not authorized"));

        Exception exception = assertThrows(AuthorizationException.class, () -> userController.getAllUsers(httpRequest));

        assertEquals("Not authorized", exception.getMessage());
        verify(userService).getAllUsers(httpRequest);
    }

    @Test
    @DisplayName("Update User")
    void updateUser() {
        Long userId = 1L;
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("newUsername", "newPassword");
        UserDTO userDTO = new UserDTO(1L, "newUsername", "USER");

        when(userService.updateUser(userId, updateRequest, httpRequest)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(userId, updateRequest, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService).updateUser(userId, updateRequest, httpRequest);
    }

    @Test
    @DisplayName("Update User - User Not Found")
    void updateUserNotFound() {
        Long userId = 1L;
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO("newUsername", "newPassword");

        when(userService.updateUser(userId, updateRequest, httpRequest)).thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userController.updateUser(userId, updateRequest, httpRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userService).updateUser(userId, updateRequest, httpRequest);
    }

    @Test
    @DisplayName("Delete User")
    void deleteUser() {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId, httpRequest);

        ResponseEntity<Void> response = userController.deleteUser(userId, httpRequest);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(userId, httpRequest);
    }

    @Test
    @DisplayName("Delete User - User Not Found")
    void deleteUserNotFound() {
        Long userId = 1L;

        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(userId, httpRequest);

        Exception exception = assertThrows(UserNotFoundException.class, () -> userController.deleteUser(userId, httpRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userService).deleteUser(userId, httpRequest);
    }

    @Test
    @DisplayName("Delete User - Unauthorized")
    void deleteUserUnauthorized() {
        Long userId = 1L;

        doThrow(new AuthorizationException("Not authorized")).when(userService).deleteUser(userId, httpRequest);

        Exception exception = assertThrows(AuthorizationException.class, () -> userController.deleteUser(userId, httpRequest));

        assertEquals("Not authorized", exception.getMessage());
        verify(userService).deleteUser(userId, httpRequest);
    }
}
