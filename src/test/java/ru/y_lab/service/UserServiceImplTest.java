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
        userService = new UserServiceImpl(inputReader, userRepository, authUtil);
        userService.setCurrentUser(null);
    }

    /**
     * Test case for {@link UserServiceImpl#registerUser()} when registration is successful.
     * Validates that a user with valid credentials can be registered.
     */
    @Test
    @DisplayName("Test Register User Successfully")
    public void testRegisterUser_Success() {
        when(inputReader.readLine()).thenReturn("validUsername", "validPassword@123");
        User user = new User(2L, "validUsername", "validPassword@123", "USER");
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
    @DisplayName("Test Register User With Invalid Username")
    public void testRegisterUser_InvalidUsername() {
        when(inputReader.readLine())
                .thenReturn("in")
                .thenReturn("validPassword@123");

        User registeredUser = userService.registerUser();

        assertNull(registeredUser);
        verify(userRepository, never()).addUser(any(User.class));
    }

    /**
     * Test case for {@link UserServiceImpl#registerUser()} when password is invalid.
     * Verifies that user registration does not proceed with an invalid password.
     */
    @Test
    @DisplayName("Test Register User With Invalid Password")
    public void testRegisterUser_InvalidPassword() {
        when(inputReader.readLine())
                .thenReturn("validUsername")
                .thenReturn("short");  // Invalid password

        userService.registerUser();

        verify(userRepository, never()).addUser(any(User.class));
    }

    /**
     * Test case for {@link UserServiceImpl#registerUser()} when username is taken.
     * Verifies that user registration fails if the username is already taken.
     */
    @Test
    @DisplayName("Test Register User With Username Already Taken")
    public void testRegisterUser_UsernameTaken() throws UserNotFoundException {
        when(inputReader.readLine()).thenReturn("existingUsername", "validPassword@123");
        when(userRepository.getUserByUsername("existingUsername")).thenReturn(new User(1L, "existingUsername", "somePassword", "USER"));

        User registeredUser = userService.registerUser();

        assertNull(registeredUser);
        verify(userRepository, never()).addUser(any(User.class));
    }

    /**
     * Test case for {@link UserServiceImpl#loginUser()} when login is successful.
     * Validates that a user with correct credentials can successfully log in.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Login User Successfully")
    public void testLoginUser_Success() throws UserNotFoundException {
        when(inputReader.readLine()).thenReturn("validUsername", "validPassword@123");

        User user = new User(2L, "validUsername", "validPassword@123", "USER");

        when(authUtil.authenticate("validUsername", "validPassword@123")).thenReturn(true);
        when(userRepository.getUserByUsername("validUsername")).thenReturn(user);

        userService.loginUser();

        assertEquals(user, userService.getCurrentUser());
        verify(userRepository, times(1)).getUserByUsername("validUsername");
    }

    /**
     * Test case for {@link UserServiceImpl#loginUser()} when login fails due to incorrect password.
     * Verifies that login fails and no user is set as the current user.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Login User Failure Due to Incorrect Password")
    public void testLoginUser_Failure() throws UserNotFoundException {
        when(inputReader.readLine()).thenReturn("validUsername", "validPassword@123");

        when(authUtil.authenticate("validUsername", "invalidPassword")).thenReturn(false);

        userService.loginUser();

        assertNull(userService.getCurrentUser());
        verify(userRepository, never()).getUserByUsername("validUsername");
    }

    /**
     * Test case for {@link UserServiceImpl#loginUser()} when user is not found.
     * Verifies that login fails when the user does not exist.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Login User When User Not Found")
    public void testLoginUser_UserNotFound() throws UserNotFoundException {
        when(inputReader.readLine()).thenReturn("nonExistentUsername", "validPassword@123");

        when(authUtil.authenticate("nonExistentUsername", "validPassword@123")).thenReturn(true);
        when(userRepository.getUserByUsername("nonExistentUsername")).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userService.loginUser());
        assertNull(userService.getCurrentUser());
    }

    /**
     * Test case for {@link UserServiceImpl#getCurrentUser()}.
     * Validates retrieval of the current user set in the service.
     */
    @Test
    @DisplayName("Test Getting Current User")
    public void testGetCurrentUser() {
        User user = new User(2L, "validUsername", "validPassword@123", "USER");
        userService.setCurrentUser(user);

        User currentUser = userService.getCurrentUser();

        assertEquals(user, currentUser);
    }

    /**
     * Test case for {@link UserServiceImpl#viewAllUsers()} when no users are available.
     * Verifies that viewing all users behaves correctly when there are no users in the repository.
     */
    @Test
    @DisplayName("Test Viewing All Users When No Users Available")
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
    @DisplayName("Test Viewing All Users With Users Available")
    public void testViewAllUsers_WithUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(2L, "user1", "password1", "USER"));
        users.add(new User(2L, "user2", "password2", "USER"));
        when(userRepository.getAllUsers()).thenReturn(users);

        userService.viewAllUsers();

        verify(userRepository, times(1)).getAllUsers();
    }

    /**
     * Test case for {@link UserServiceImpl#getUserById(Long)} when the user is found.
     * Validates retrieval of a user by ID from the repository.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Getting User By ID When User Found")
    public void testGetUserById_Found() throws UserNotFoundException {
        User user = new User(2L, "validUsername", "validPassword@123", "USER");
        when(userRepository.getUserById(2L)).thenReturn(user);

        User foundUser = userService.getUserById(2L);

        assertEquals(user, foundUser);
        verify(userRepository, times(1)).getUserById(2L);
    }

    /**
     * Test case for {@link UserServiceImpl#getUserById(Long)} when the user is not found.
     * Verifies that attempting to get a non-existent user throws {@link UserNotFoundException}.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Getting User By ID When User Not Found")
    public void testGetUserById_NotFound() throws UserNotFoundException {
        when(userRepository.getUserById(999L)).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).getUserById(999L);
    }
}


