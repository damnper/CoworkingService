package ru.y_lab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.util.AuthenticationUtil;
import ru.y_lab.util.InputReader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@DisplayName("Unit Tests for UserServiceImplTest")
public class UserServiceImplTest {

    @Mock
    private InputReader inputReader;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationUtil authUtil;

    private UserServiceImpl userService;
    /**
     * Sets up the test environment by initializing mocks and creating an instance of {@link UserServiceImpl}.
     * Sets the current user to null for each test case.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(inputReader, userRepository, false);
        userService.setCurrentUser(null);
    }

    /**
     * Test case for {@link UserServiceImpl#registerUser()} when registration is successful.
     * Validates that a user with valid credentials can be registered.
     */
    @Test
    public void testRegisterUser_Success() {
        when(inputReader.readLine()).thenReturn("validUsername", "validPassword@123");
        User user = new User("123", "validUsername", "validPassword@123", "USER");
        when(userRepository.addUser(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser();

        assertNotNull(registeredUser);
        assertEquals("validUsername", registeredUser.getUsername());
        assertEquals("validPassword@123", registeredUser.getPassword());
        assertEquals("USER", registeredUser.getRole());

        verify(userRepository, times(1)).addUser(any(User.class));
    }

    /**
     * Test case for {@link UserServiceImpl#registerUser()} when username is invalid.
     * Verifies that user registration does not proceed with an invalid username.
     */
    @Test
    public void testRegisterUser_InvalidUsername() {
        when(inputReader.readLine())
                .thenReturn("in")  // Invalid username
                .thenReturn("validPassword@123");

        userService.registerUser();

        verify(userRepository, never()).addUser(any(User.class));
    }

    /**
     * Test case for {@link UserServiceImpl#registerUser()} when password is invalid.
     * Verifies that user registration does not proceed with an invalid password.
     */
    @Test
    public void testRegisterUser_InvalidPassword() {
        when(inputReader.readLine())
                .thenReturn("validUsername")
                .thenReturn("short");  // Invalid password

        userService.registerUser();

        verify(userRepository, never()).addUser(any(User.class));
    }

    /**
     * Test case for {@link UserServiceImpl#loginUser()} when login is successful.
     * Validates that a user with correct credentials can successfully log in.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testLoginUser_Success() throws UserNotFoundException {
        when(inputReader.readLine()).thenReturn("validUsername", "validPassword@123");

        User user = new User("123", "validUsername", "validPassword@123", "USER");

        when(authUtil.authenticate("validUsername", "validPassword@123")).thenReturn(true);
        when(userRepository.getUserByUsername("validUsername")).thenReturn(user);

        userService.loginUser();

        assertEquals(user, userService.getCurrentUser());
        verify(userRepository, times(2)).getUserByUsername("validUsername");
    }

    /**
     * Test case for {@link UserServiceImpl#loginUser()} when login fails due to incorrect password.
     * Verifies that login fails and no user is set as the current user.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testLoginUser_Failure() throws UserNotFoundException {
        when(inputReader.readLine()).thenReturn("validUsername", "validPassword@123");

        when(authUtil.authenticate("validUsername", "invalidPassword")).thenReturn(false);

        userService.loginUser();

        assertNull(userService.getCurrentUser());
        verify(userRepository, times(1)).getUserByUsername("validUsername");
    }

    /**
     * Test case for {@link UserServiceImpl#getCurrentUser()}.
     * Validates retrieval of the current user set in the service.
     */
    @Test
    public void testGetCurrentUser() {
        User user = new User("123", "validUsername", "validPassword@123", "USER");
        userService.setCurrentUser(user);

        User currentUser = userService.getCurrentUser();

        assertEquals(user, currentUser);
    }

    /**
     * Test case for {@link UserServiceImpl#viewAllUsers()} when no users are available.
     * Verifies that viewing all users behaves correctly when there are no users in the repository.
     */
    @Test
    public void testViewAllUsers_NoUsers() {
        when(userRepository.getAllUsers()).thenReturn(new ArrayList<>());

        userService.viewAllUsers();

        verify(userRepository, times(1)).getAllUsers();
    }

    /**
     * Test case for {@link UserServiceImpl#viewAllUsers()} when users are available.
     * Verifies that viewing all users behaves correctly when there are users in the repository.
     */
    @Test
    public void testViewAllUsers_WithUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(UUID.randomUUID().toString(), "user1", "password1", "USER"));
        users.add(new User(UUID.randomUUID().toString(), "user2", "password2", "USER"));
        when(userRepository.getAllUsers()).thenReturn(users);

        userService.viewAllUsers();

        verify(userRepository, times(1)).getAllUsers();
    }

    /**
     * Test case for {@link UserServiceImpl#getUserById(String)}.
     * Validates retrieval of a user by ID from the repository.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testGetUserById() throws UserNotFoundException {
        User user = new User("123", "validUsername", "validPassword@123", "USER");
        when(userRepository.getUserById("123")).thenReturn(user);

        User foundUser = userService.getUserById("123");

        assertEquals(user, foundUser);
        verify(userRepository, times(1)).getUserById("123");
    }
}


