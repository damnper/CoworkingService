package ru.y_lab.repo;


import ru.y_lab.config.DatabaseManager;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * The UserRepository class provides methods to manage users.
 * It stores user data in a HashMap and supports CRUD operations.
 */
public class UserRepository {

    /**
     * Adds a new user to the repository.
     * @param user the user to be added
     * @return the user with the specified ID
     */
    public User addUser(User user) {
        String sql = "INSERT INTO coworking_service.users (id, username, password, role) VALUES (DEFAULT, (?), (?), (?))";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    return user;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding user", e);
        }
    }

    /**
     * Retrieves a user by their ID.
     * @param id the ID of the user
     * @return the user with the specified ID
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    public User getUserById(Long id) throws UserNotFoundException {
        String sql = "SELECT * FROM coworking_service.users WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    throw new UserNotFoundException("User with ID " + id + " not found");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user by ID", e);
        }
    }

    /**
     * Retrieves a user by their username.
     * @param username the username of the user
     * @return the user with the specified username
     * @throws UserNotFoundException if the user with the specified username is not found
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        String sql = "SELECT * FROM coworking_service.users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    throw new UserNotFoundException("User with username " + username + " not found");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user by username", e);
        }
    }

    /**
     * Updates an existing user in the repository.
     * @param user the user with updated information
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    public void updateUser(User user) throws UserNotFoundException {
        String sql = "UPDATE coworking_service.users SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setLong(4, user.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("User with ID " + user.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating user", e);
        }
    }

    /**
     * Deletes a user from the repository by their ID.
     * @param id the ID of the user to be deleted
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    public void deleteUser(String id) throws UserNotFoundException {
        String sql = "DELETE FROM coworking_service.users WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(id));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException("User with ID " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting user", e);
        }
    }

    /**
     * Retrieves all users from the repository.
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM coworking_service.users";
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving all users", e);
        }
        return users;
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

