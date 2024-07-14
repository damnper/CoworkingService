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
import ru.y_lab.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("Delete User")
    void deleteUser() {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId, httpRequest);

        ResponseEntity<Void> response = userController.deleteUser(userId, httpRequest);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(userId, httpRequest);
    }
}
