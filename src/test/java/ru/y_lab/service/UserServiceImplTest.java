package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomUserMapper;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.util.AuthenticationUtil;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceImpl Tests")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserMapper userMapper;

    @Mock
    private AuthenticationUtil authUtil;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserServiceImpl userService;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userMapper, userRepository, authUtil);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
        when(req.getSession()).thenReturn(session);
    }

    /**
     * Test for registering a new user.
     * This test verifies that a new user is correctly registered and a success response is sent.
     */
    @Test
    @DisplayName("Register User")
    void registerUser() throws IOException {
        String requestBody = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(req.getReader()).thenReturn(reader);

        User registeredUser = User.builder().id(1L).username("testuser").password("testpass").role("USER").build();
        UserDTO userDTO = new UserDTO(1L, "testuser", "testpass", "USER");

        when(userRepository.addUser(any(User.class))).thenReturn(registeredUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        userService.registerUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(201, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"username\":\"testuser\",\"password\":\"testpass\",\"role\":\"USER\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling invalid register request.
     * This test verifies that an error response is sent when the request data is invalid.
     */
    @Test
    @DisplayName("Register User with Invalid Data")
    void registerUser_withInvalidData() throws IOException {
        String requestBody = "{\"username\": \"\", \"password\": \"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(req.getReader()).thenReturn(reader);

        userService.registerUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(400, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling internal server error during register.
     * This test verifies that an error response is sent when an internal server error occurs.
     */
    @Test
    @DisplayName("Register User with Internal Server Error")
    void registerUser_withInternalServerError() throws IOException {
        String requestBody = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(req.getReader()).thenReturn(reader);

        doThrow(new RuntimeException("Unexpected error")).when(userRepository).addUser(any(User.class));

        userService.registerUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(500, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }


    /**
     * Test for logging in a user.
     * This test verifies that a user is successfully authenticated and a success response is sent.
     */
    @Test
    @DisplayName("Login User")
    void loginUser() throws IOException, UserNotFoundException {
        String requestBody = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(req.getReader()).thenReturn(reader);

        UserDTO userDTO = new UserDTO(1L, "testuser", "testpass", "USER");

        when(authUtil.authenticateUser(any(LoginRequestDTO.class))).thenReturn(userDTO);

        userService.loginUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"username\":\"testuser\",\"password\":\"testpass\",\"role\":\"USER\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling invalid login request.
     * This test verifies that an error response is sent when the request data is invalid.
     */
    @Test
    @DisplayName("Login User with Invalid Data")
    void loginUser_withInvalidData() throws IOException {
        String requestBody = "{\"username\": \"\", \"password\": \"\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(req.getReader()).thenReturn(reader);

        userService.loginUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(400, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling login with non-existent user.
     * This test verifies that an error response is sent when the user is not found.
     */
    @Test
    @DisplayName("Login User Not Found")
    void loginUser_userNotFound() throws IOException, UserNotFoundException {
        String requestBody = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(req.getReader()).thenReturn(reader);

        when(authUtil.authenticateUser(any(LoginRequestDTO.class))).thenThrow(new UserNotFoundException("User not found"));

        userService.loginUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling internal server error during login.
     * This test verifies that an error response is sent when an internal server error occurs.
     */
    @Test
    @DisplayName("Login User with Internal Server Error")
    void loginUser_withInternalServerError() throws IOException, UserNotFoundException {
        String requestBody = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        BufferedReader reader = new BufferedReader(new StringReader(requestBody));
        when(req.getReader()).thenReturn(reader);

        doThrow(new RuntimeException("Unexpected error")).when(authUtil).authenticateUser(any(LoginRequestDTO.class));

        userService.loginUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(500, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }

    /**
     * Test for retrieving a user by their ID.
     * This test verifies that a user is successfully retrieved and a success response is sent.
     */
    @Test
    @DisplayName("Get User by ID")
    void getUserById() throws IOException, UserNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        UserDTO userDTO = new UserDTO(2L, "testuser", "testpass", "USER");
        User user = User.builder().id(2L).username("testuser").password("testpass").role("USER").build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(true);
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        userService.getUserById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":2,\"username\":\"testuser\",\"password\":\"testpass\",\"role\":\"USER\"}", stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access")
    void getUserById_unauthorized() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(false);

        userService.getUserById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for user not found.
     * This test verifies that an error response is sent when the user is not found.
     */
    @Test
    @DisplayName("User Not Found")
    void getUserById_userNotFound() throws IOException, UserNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(true);
        when(userRepository.getUserById(2L)).thenReturn(Optional.empty());

        userService.getUserById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling general exceptions.
     * This test verifies that an error response is sent when a general exception occurs.
     */
    @Test
    @DisplayName("Handle General Exception")
    void getUserById_generalException() throws IOException, UserNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(true);
        when(userRepository.getUserById(2L)).thenThrow(new RuntimeException("Unexpected error"));

        userService.getUserById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(400, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unexpected error\"}", stringWriter.toString().trim());
    }

    /**
     * Test for retrieving all users.
     * This test verifies that all users are successfully retrieved and a success response is sent.
     */
    @Test
    @DisplayName("Get All Users")
    void getAllUsers() throws IOException {
        List<User> userList = List.of(
                User.builder().id(1L).username("user1").password("pass1").role("USER").build(),
                User.builder().id(2L).username("user2").password("pass2").role("USER").build()
        );
        List<UserDTO> userDTOList = List.of(
                new UserDTO(1L, "user1", "pass1", "USER"),
                new UserDTO(2L, "user2", "pass2", "USER")
        );

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(new UserDTO(1L, "admin", "adminpass", "ADMIN"));
        when(userRepository.getAllUsers()).thenReturn(userList);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTOList.get(0), userDTOList.get(1));

        userService.getAllUsers(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("[{\"id\":1,\"username\":\"user1\",\"password\":\"pass1\",\"role\":\"USER\"},{\"id\":2,\"username\":\"user2\",\"password\":\"pass2\",\"role\":\"USER\"}]", stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access")
    void getAllUsers_unauthorized() throws IOException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenThrow(new SecurityException("Access denied"));

        userService.getAllUsers(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling general exceptions.
     * This test verifies that an error response is sent when a general exception occurs.
     */
    @Test
    @DisplayName("Handle General Exception")
    void getAllUsers_generalException() throws IOException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), eq("ADMIN"))).thenReturn(new UserDTO(1L, "admin", "adminpass", "ADMIN"));
        when(userRepository.getAllUsers()).thenThrow(new RuntimeException("Unexpected error"));

        userService.getAllUsers(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(400, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unexpected error\"}", stringWriter.toString().trim());
    }

    /**
     * Test for updating an existing user.
     * This test verifies that a user is successfully updated and a success response is sent.
     */
    @Test
    @DisplayName("Update User")
    void updateUser() throws IOException, UserNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        UserDTO userDTO = new UserDTO(2L, "testuser", "newpass", "USER");
        User user = User.builder().id(2L).username("testuser").password("newpass").role("USER").build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(true);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\": \"testuser\", \"password\": \"newpass\"}")));
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(user));
        when(userRepository.updateUser(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        userService.updateUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":2,\"username\":\"testuser\",\"password\":\"newpass\",\"role\":\"USER\"}", stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access")
    void updateUser_unauthorized() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(false);

        userService.updateUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for user not found.
     * This test verifies that an error response is sent when the user is not found.
     */
    @Test
    @DisplayName("User Not Found")
    void updateUser_userNotFound() throws IOException, UserNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\": \"testuser\", \"password\": \"newpass\"}")));
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(true);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\": \"testuser\", \"password\": \"newpass\"}")));
        when(userRepository.getUserById(2L)).thenReturn(Optional.empty());

        userService.updateUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"User not found by ID: 2\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling invalid update request.
     * This test verifies that an error response is sent when the update data is invalid.
     */
    @Test
    @DisplayName("Invalid Update Request")
    void updateUser_invalidRequest() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(true);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\": \"testuser\", \"password\": \"newpass\"}")));
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"username\": \"\", \"password\": \"\"}")));

        userService.updateUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(400, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.\"}", stringWriter.toString().trim());
    }


    /**
     * Test for successfully deleting a user.
     * This test verifies that a user is successfully deleted and a success response is sent.
     */
    @Test
    @DisplayName("Delete User")
    void deleteUser() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(true);

        userService.deleteUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(204, statusCaptor.getValue());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized to delete the user.
     */
    @Test
    @DisplayName("Unauthorized Access")
    void deleteUser_unauthorized() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("userId")).thenReturn("2");
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(false);

        userService.deleteUser(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }
}
