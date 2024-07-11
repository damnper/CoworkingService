package ru.y_lab.repo;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.y_lab.config.DatabaseConfig;
import ru.y_lab.config.DatabaseManager;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserRepository class.
 */
@Testcontainers
@DisplayName("Unit Tests for UserRepositoryTest")
public class UserRepositoryTest {

    private UserRepository userRepository;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    public static void setUpBeforeAll() {
        postgresContainer.start();
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setDriverClassName("org.postgresql.Driver");
        dbConfig.setUrl(postgresContainer.getJdbcUrl());
        dbConfig.setUsername(postgresContainer.getUsername());
        dbConfig.setPassword(postgresContainer.getPassword());
        DatabaseManager.setConfig(dbConfig);

        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE SCHEMA IF NOT EXISTS coworking_service");

                stmt.execute("SET search_path TO coworking_service");

                stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                        + "id SERIAL PRIMARY KEY, "
                        + "username VARCHAR(255) UNIQUE NOT NULL, "
                        + "password VARCHAR(255) NOT NULL, "
                        + "role VARCHAR(255) NOT NULL"
                        + ")");}
        } catch (SQLException e) {
            throw new RuntimeException("Error setting up test database", e);
        }
    }

    @AfterAll
    public static void tearDownAfterAll() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP SCHEMA IF EXISTS coworking_service CASCADE");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error tearing down test database", e);
        }
    }

    /**
     * Sets up the test environment by creating a new instance of UserRepository before each test.
     */
    @BeforeEach
    public void setUp() {
        userRepository = new UserRepository();
    }

    @AfterEach
    public void tearDown() {
        // Убедитесь, что база данных очищена после каждого теста
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("TRUNCATE TABLE coworking_service.users RESTART IDENTITY CASCADE");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error tearing down the database after test", e);
        }
    }

    /**
     * Test case for adding a user to the repository.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Adding a New User")
    public void testAddUser() throws UserNotFoundException {
        User user = new User(null, "testuser", "123456", "USER");
        User savedUser = userRepository.addUser(user);
        User retrievedUser = userRepository.getUserById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals(user.getUsername(), retrievedUser.getUsername());
    }

    /**
     * Test case for retrieving a user by ID.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Retrieving a User by ID")
    public void testGetUserById() throws UserNotFoundException {
        User user = new User(null, "testuser", "123456", "USER");
        User savedUser = userRepository.addUser(user);
        User retrievedUser = userRepository.getUserById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals(user.getUsername(), retrievedUser.getUsername());
    }

    /**
     * Test case for retrieving a user by username.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    @DisplayName("Test Retrieving a User by Username")
    public void testGetUserByUsername() throws UserNotFoundException {
        User user = new User(null, "testuser", "123456", "USER");
        userRepository.addUser(user);
        User retrievedUser = userRepository.getUserByUsername("testuser");
        assertNotNull(retrievedUser);
        assertEquals(user.getUsername(), retrievedUser.getUsername());
    }

    /**
     * Test case for updating an existing user.
     *
     * @throws UserNotFoundException if the user to update is not found
     */
    @Test
    @DisplayName("Test Updating an Existing User")
    public void testUpdateUser() throws UserNotFoundException {
        User user = new User(null, "testuser", "123456", "USER");
        User savedUser = userRepository.addUser(user);
        savedUser.setUsername("updatedUser");
        userRepository.updateUser(savedUser);
        User retrievedUser = userRepository.getUserById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals("updatedUser", retrievedUser.getUsername());
    }

    /**
     * Test case for deleting an existing user.
     */
    @Test
    @DisplayName("Test Deleting an Existing User")
    public void testDeleteUser() {
        User user = new User(null, "testuser", "123456", "USER");
        User savedUser = userRepository.addUser(user);
        assertDoesNotThrow(() -> userRepository.deleteUser(savedUser.getId()));
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserById(savedUser.getId()));
    }

    /**
     * Test case for retrieving all users from the repository.
     */
    @Test
    @DisplayName("Test Retrieving All Users")
    public void testGetAllUsers() {
        User user1 = new User(null, "testuser1", "123456", "USER");
        User user2 = new User(null, "testuser2", "123456", "USER");
        userRepository.addUser(user1);
        userRepository.addUser(user2);
        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals("testuser1")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getUsername().equals("testuser2")));
    }

    /**
     * Test case for retrieving a user by ID that does not exist, expecting a UserNotFoundException.
     */
    @Test
    @DisplayName("Test Retrieving Non-Existent User by ID")
    public void testGetUserByIdNotFound() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserById(999L));
    }

    /**
     * Test case for retrieving a user by username that does not exist, expecting a UserNotFoundException.
     */
    @Test
    @DisplayName("Test Retrieving Non-Existent User by Username")
    public void testGetUserByUsernameNotFound() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserByUsername("nonexistent_username"));
    }

    /**
     * Test case for updating a user that does not exist, expecting a UserNotFoundException.
     */
    @Test
    @DisplayName("Test Updating Non-Existent User")
    public void testUpdateNonExistentUser() {
        User nonExistentUser = new User(999L, "testuser", "123456", "USER");
        assertThrows(UserNotFoundException.class, () -> userRepository.updateUser(nonExistentUser));
    }

    /**
     * Test case for deleting a user that does not exist, expecting a UserNotFoundException.
     * Also verifies that the repository remains unchanged (no users were deleted).
     */
    @Test
    @DisplayName("Test Deleting Non-Existent User")
    public void testDeleteNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> userRepository.deleteUser(999L));
        List<User> allUsers = userRepository.getAllUsers();
        assertEquals(0, allUsers.size());
    }
}
