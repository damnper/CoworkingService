package ru.y_lab.repo;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.y_lab.exception.DatabaseException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The UserRepository class provides methods to manage users.
 * It interacts with the database to perform CRUD operations for users.
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DataSource dataSource;

    // SQL Queries as constants
    private static final String ADD_USER_SQL = "INSERT INTO coworking_service.users (id, username, password, role) VALUES (DEFAULT, ?, ?, ?)";
    private static final String GET_USER_BY_ID_SQL = "SELECT * FROM coworking_service.users WHERE id = ?";
    private static final String GET_USER_BY_USERNAME_SQL = "SELECT * FROM coworking_service.users WHERE username = ?";
    private static final String UPDATE_USER_SQL = "UPDATE coworking_service.users SET username = ?, password = ?, role = ? WHERE id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM coworking_service.users WHERE id = ?";
    private static final String GET_ALL_USERS_SQL = "SELECT * FROM coworking_service.users";

    /**
     * Adds a new user to the repository.
     * @param user the user to be added
     * @return the user with the specified ID
     */
    public User addUser(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(ADD_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("User creation failed. No rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    return user;
                } else {
                    throw new DatabaseException("User creation failed. No ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while adding the user. Please try again later.");
        }
    }

    /**
     * Retrieves a user by their ID.
     * @param id the ID of the user
     * @return the user with the specified ID
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    public Optional<User> getUserById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_USER_BY_ID_SQL)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapRowToUser(rs);
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving the user by ID. Please try again later.");
        }
    }

    /**
     * Retrieves a user by their username.
     * @param username the username of the user
     * @return the user with the specified username
     */
    public Optional<User> getUserByUsername(String username) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_USER_BY_USERNAME_SQL)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapRowToUser(rs);
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving the user by username. Please try again later.");
        }
    }

    /**
     * Retrieves all users from the repository.
     * @return a list of all users
     */
    public Optional<List<User>> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_ALL_USERS_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving all users. Please try again later.");
        }
        return users.isEmpty() ? Optional.empty() : Optional.of(users);
    }

    /**
     * Updates an existing user in the repository.
     * @param user the user with updated information
     * @return the updated user
     */
    public Optional<User> updateUser(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updatePs = connection.prepareStatement(UPDATE_USER_SQL);
             PreparedStatement selectPs = connection.prepareStatement(GET_USER_BY_ID_SQL)) {

            // Check if user exists
            selectPs.setLong(1, user.getId());
            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    throw new UserNotFoundException("User with ID " + user.getId() + " not found.");
                }
            }

            updatePs.setString(1, user.getUsername());
            updatePs.setString(2, user.getPassword());
            updatePs.setString(3, user.getRole());
            updatePs.setLong(4, user.getId());

            updatePs.executeUpdate();

            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    User updatedUser = mapRowToUser(rs);
                    return Optional.of(updatedUser);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while updating the user. Please try again later.");
        }
    }

    /**
     * Deletes a user from the repository by their ID.
     * @param id the ID of the user to be deleted
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    public void deleteUser(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_USER_SQL)) {

            ps.setLong(1, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("User with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while deleting the user. Please try again later.");
        }
    }

    /**
     * Maps a row from the ResultSet to a User object.
     * @param rs the ResultSet containing user data
     * @return a User object mapped from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .role(rs.getString("role"))
                .build();
    }
}

