package ru.y_lab.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    public void testAddUser() throws UserNotFoundException {
        User user = new User("1", "testuser", "123456","USER");
        userRepository.addUser(user);

        User retrievedUser = userRepository.getUserById("1");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    @Test
    public void testGetUserById() throws UserNotFoundException {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        User retrievedUser = userRepository.getUserById("1");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    @Test
    public void testGetUserByUsername() throws UserNotFoundException {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        User retrievedUser = userRepository.getUserByUsername("testuser");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

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

    @Test
    public void testDeleteUser() {
        User user = new User("1", "testuser", "123456", "USER");
        userRepository.addUser(user);

        assertDoesNotThrow(() -> userRepository.deleteUser("1"));

        assertThrows(UserNotFoundException.class, () -> userRepository.getUserById("1"));

    }

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

    @Test
    public void testGetUserByIdNotFound() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserById("nonexistent_id"));
    }

    @Test
    public void testGetUserByUsernameNotFound() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserByUsername("nonexistent_username"));
    }

    @Test
    public void testUpdateNonExistentUser() {
        User nonExistentUser = new User("nonexistent_id", "testuser", "123456", "USER");
        assertThrows(UserNotFoundException.class, () -> userRepository.updateUser(nonExistentUser));
    }

    @Test
    public void testDeleteNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> userRepository.deleteUser("nonexistent_id"));

        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(0, allUsers.size());
    }
}
