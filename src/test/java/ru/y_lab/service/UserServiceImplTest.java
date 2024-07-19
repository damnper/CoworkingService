package ru.y_lab.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.dto.LoginRequestDTO;
import ru.y_lab.dto.RegisterRequestDTO;
import ru.y_lab.dto.UpdateUserRequestDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.UserMapper;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.util.AuthenticationUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceImpl Tests")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationUtil authUtil;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userMapper, userRepository, authUtil);
    }

    /**
     * Test for registering a new user.
     * This test verifies that a new user is correctly registered and a success response is sent.
     */
    @Test
    @DisplayName("Register User")
    void registerUser() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("testuser", "testpass");
        User user = User.builder().username("testuser").password("testpass").role("USER").build();
        User savedUser = User.builder().id(1L).username("testuser").password("testpass").role("USER").build();
        UserDTO userDTO = new UserDTO(1L, "testuser", "USER");

        when(userRepository.addUser(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(userDTO);

        UserDTO result = userService.registerUser(requestDTO);

        assertEquals(userDTO, result);
        verify(userRepository).addUser(user);
    }

    @Test
    @DisplayName("Register User - Invalid Username")
    void registerUser_invalidUsername() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("tu", "testpass");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(requestDTO));

        assertEquals("Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.", exception.getMessage());
    }

    @Test
    @DisplayName("Register User - Invalid Password")
    void registerUser_invalidPassword() {
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("testuser", "tp");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(requestDTO));

        assertEquals("Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %.", exception.getMessage());
    }

    @Test
    @DisplayName("Login User")
    void loginUser() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("testuser", "testpass");
        User user = User.builder().username("testuser").password("testpass").role("USER").build();
        UserDTO userDTO = new UserDTO(1L, "testuser", "USER");

        when(authUtil.login(requestDTO)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(req.getSession(true)).thenReturn(session);

        UserDTO result = userService.loginUser(requestDTO, req);

        assertEquals(userDTO, result);
        verify(authUtil).login(requestDTO);
        verify(userMapper).toDTO(user);
        verify(session).setAttribute("currentUser", userDTO);
        verify(session).setMaxInactiveInterval(30 * 60);
    }

    @Test
    @DisplayName("Login User - Invalid Username")
    void loginUser_invalidUsername() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("tu", "testpass");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.loginUser(requestDTO, req));

        assertEquals("Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.", exception.getMessage());
    }

    @Test
    @DisplayName("Login User - Invalid Password")
    void loginUser_invalidPassword() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("testuser", "tp");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.loginUser(requestDTO, req));

        assertEquals("Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %.", exception.getMessage());
    }

    @Test
    @DisplayName("Login User - User Not Found")
    void loginUser_userNotFound() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("testuser", "testpass");

        when(authUtil.login(requestDTO)).thenThrow(new UserNotFoundException("User not found with the provided credentials: testuser"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.loginUser(requestDTO, req));

        assertEquals("User not found with the provided credentials: testuser", exception.getMessage());
    }

    @Test
    @DisplayName("Login User - Invalid Credentials")
    void loginUser_invalidCredentials() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("testuser", "");
        User user = User.builder().username("testuser").password("testpass").role("USER").build();

        when(userRepository.getUserByUsername(requestDTO.username())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.loginUser(requestDTO, req));

        assertEquals("Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %.", exception.getMessage());
    }

    @Test
    @DisplayName("Get User by ID")
    void getUserById() {
        User user = User.builder().id(1L).username("testuser").password("testpass").role("USER").build();
        UserDTO userDTO = new UserDTO(1L, "testuser", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(userDTO);
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L, req);

        assertEquals(userDTO, result);
        verify(userRepository).getUserById(1L);
        verify(userMapper).toDTO(user);
    }

    @Test
    @DisplayName("Get User by ID - User Not Found")
    void getUserById_userNotFound() {
        when(authUtil.authenticate(req, "USER")).thenReturn(new UserDTO(1L, "admin", "ADMIN"));
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L, req));

        assertEquals("User not found by ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Get All Users")
    void getAllUsers() {
        UserDTO admin = new UserDTO(1L, "admin", "ADMIN");
        List<User> users = List.of(
                User.builder().id(2L).username("user1").password("pass1").role("USER").build(),
                User.builder().id(3L).username("user2").password("pass2").role("USER").build()
        );
        List<UserDTO> userDTOs = List.of(
                new UserDTO(2L, "user1", "USER"),
                new UserDTO(3L, "user2", "USER")
        );

        when(authUtil.authenticate(req, "ADMIN")).thenReturn(admin);
        when(userRepository.getAllUsers()).thenReturn(Optional.of(users));
        when(userMapper.toDTO(users.get(0))).thenReturn(userDTOs.get(0));
        when(userMapper.toDTO(users.get(1))).thenReturn(userDTOs.get(1));

        List<UserDTO> result = userService.getAllUsers(req);

        assertEquals(userDTOs, result);
        verify(userRepository).getAllUsers();
        verify(userMapper).toDTO(users.get(0));
        verify(userMapper).toDTO(users.get(1));
    }

    @Test
    @DisplayName("Get All Users - No Users Found")
    void getAllUsers_noUsersFound() {
        UserDTO adminUser = new UserDTO(1L, "admin", "ADMIN");
        when(authUtil.authenticate(req, "ADMIN")).thenReturn(adminUser);
        when(userRepository.getAllUsers()).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getAllUsers(req));

        assertEquals("No users found in the system.", exception.getMessage());
    }

    @Test
    @DisplayName("Update User")
    void updateUser() {
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO("updatedUser", "newpass");
        User user = User.builder().id(1L).username("testuser").password("testpass").role("USER").build();
        User updatedUser = User.builder().id(1L).username("updatedUser").password("newpass").role("USER").build();
        UserDTO userDTO = new UserDTO(1L, "user", "USER");
        UserDTO updatedUserDTO = new UserDTO(1L, "updatedUser", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(userDTO);
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.updateUser(updatedUser)).thenReturn(Optional.of(updatedUser));
        when(userMapper.toDTO(updatedUser)).thenReturn(updatedUserDTO);

        UserDTO result = userService.updateUser(1L, requestDTO, req);

        assertEquals(updatedUserDTO, result);
        verify(userRepository).getUserById(1L);
        verify(userRepository).updateUser(updatedUser);
        verify(userMapper).toDTO(updatedUser);
    }

    @Test
    @DisplayName("Update User - User Not Found")
    void updateUser_userNotFound() {
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO("updatedUser", "newpass");
        UserDTO userDTO = new UserDTO(1L, "user", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(userDTO);
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, requestDTO, req));

        assertEquals("User not found by ID: 1", exception.getMessage());
        verify(userRepository).getUserById(1L);
    }

    @Test
    @DisplayName("Update User - Update Failed")
    void updateUser_updateFailed() {
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO("updatedUser", "newpass");
        User user = User.builder().id(1L).username("testuser").password("testpass").role("USER").build();
        UserDTO userDTO = new UserDTO(1L, "user", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(userDTO);
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.updateUser(user)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, requestDTO, req));

        assertEquals("User not found after update by ID: 1", exception.getMessage());
        verify(userRepository).getUserById(1L);
        verify(userRepository).updateUser(user);
    }

    @Test
    @DisplayName("Delete User")
    void deleteUser() {
        User user = User.builder().id(1L).username("testuser").password("testpass").role("USER").build();
        UserDTO currentUser = new UserDTO(1L, "testuser", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(req.getSession(false)).thenReturn(session);
        when(userRepository.getUserById(currentUser.id())).thenReturn(Optional.of(user));

        userService.deleteUser(1L, req);

        verify(userRepository).deleteUser(1L);
        verify(session).invalidate();
    }

    @Test
    @DisplayName("Delete User - User Not Found")
    void deleteUser_userNotFound() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L, req));

        assertEquals("User not found for delete by ID: 1", exception.getMessage());
        verify(userRepository).getUserById(1L);
    }

    @Test
    @DisplayName("Delete User - Deletion Failed")
    void deleteUser_deletionFailed() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");
        User user = User.builder().id(1L).username("testuser").password("testpass").role("USER").build();

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Deletion failed")).when(userRepository).deleteUser(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L, req));

        assertEquals("Deletion failed", exception.getMessage());
        verify(userRepository).getUserById(1L);
        verify(userRepository).deleteUser(1L);
    }
}
