package ru.y_lab.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserRepository class.
 */
@DisplayName("Unit Tests for UserRepositoryTest")
public class UserRepositoryTest {
    private UserRepository userRepository;

    /**
     * Sets up the test environment by creating a new instance of UserRepository before each test.
     */
    @BeforeEach
    public void setUp() {
        userRepository = new UserRepository();
    }
    /**
     * Test case for adding a user to the repository.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testAddUser() throws UserNotFoundException {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        User retrievedUser = userRepository.getUserById("1");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    /**
     * Test case for retrieving a user by ID.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testGetUserById() throws UserNotFoundException {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        User retrievedUser = userRepository.getUserById("1");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    /**
     * Test case for retrieving a user by username.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testGetUserByUsername() throws UserNotFoundException {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        User retrievedUser = userRepository.getUserByUsername("testuser");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    /**
     * Test case for updating an existing user.
     *
     * @throws UserNotFoundException if the user to update is not found
     */
    @Test
    public void testUpdateUser() throws UserNotFoundException {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        User updatedUser = new User("1", "updatedUser", "123456", "USER");
        userRepository.updateUser(updatedUser);

        User retrievedUser = userRepository.getUserById("1");
        assertNotNull(retrievedUser);
        assertEquals(updatedUser, retrievedUser);
    }

    /**
     * Test case for deleting an existing user.
     */
    @Test
    public void testDeleteUser() {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        assertDoesNotThrow(() -> userRepository.deleteUser("1"));

        assertThrows(UserNotFoundException.class, () -> userRepository.getUserById("1"));
    }

    /**
     * Test case for retrieving all users from the repository.
     */
    @Test
    public void testGetAllUsers() {
        User user1 = new User("1", "testuser1", "123456", "USER");
        User user2 = new User("2", "testuser2", "123456", "USER");
        userRepository.addUser(user1);
        userRepository.addUser(user2);

        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    /**
     * Test case for retrieving a user by ID that does not exist, expecting a UserNotFoundException.
     */
    @Test
    public void testGetUserByIdNotFound() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserById("nonexistent_id"));
    }

    /**
     * Test case for retrieving a user by username that does not exist, expecting a UserNotFoundException.
     */
    @Test
    public void testGetUserByUsernameNotFound() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserByUsername("nonexistent_username"));
    }

    /**
     * Test case for updating a user that does not exist, expecting a UserNotFoundException.
     */
    @Test
    public void testUpdateNonExistentUser() {
        User nonExistentUser = new User("nonexistent_id", "testuser", "123456", "USER");
        assertThrows(UserNotFoundException.class, () -> userRepository.updateUser(nonExistentUser));
    }

    /**
     * Test case for deleting a user that does not exist, expecting a UserNotFoundException.
     * Also verifies that the repository remains unchanged (no users were deleted).
     */
    @Test
    public void testDeleteNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> userRepository.deleteUser("nonexistent_id"));

        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(0, allUsers.size());
    }
}
