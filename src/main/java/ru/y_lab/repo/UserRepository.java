package ru.y_lab.repo;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The UserRepository class provides methods to manage users.
 * It stores user data in a HashMap and supports CRUD operations.
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DataSource dataSource;

    /**
     * Adds a new user to the repository.
     * @param user the user to be added
     * @return the user with the specified ID
     */
    public User addUser(User user) {
        String sql = "INSERT INTO coworking_service.users (id, username, password, role) VALUES (DEFAULT, (?), (?), (?))";
        try (Connection connection = dataSource.getConnection();
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
    public Optional<User> getUserById(Long id) throws UserNotFoundException {
        String sql = "SELECT * FROM coworking_service.users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
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
            throw new RuntimeException("Error while retrieving user by ID", e);
        }
    }

    /**
     * Retrieves a user by their username.
     * @param username the username of the user
     * @return the user with the specified username
     */
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM coworking_service.users WHERE username = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
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
            throw new RuntimeException("Error while retrieving user by username", e);
        }
    }

    /**
     * Updates an existing user in the repository.
     * @param user the user with updated information
     */
    public Optional<User> updateUser(User user) {
        String updateSql = "UPDATE coworking_service.users SET username = ?, password = ?, role = ? WHERE id = ?";
        String selectSql = "SELECT * FROM coworking_service.users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement updatePs = connection.prepareStatement(updateSql);
             PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
            updatePs.setString(1, user.getUsername());
            updatePs.setString(2, user.getPassword());
            updatePs.setString(3, user.getRole());
            updatePs.setLong(4, user.getId());

            updatePs.executeUpdate();

            selectPs.setLong(1, user.getId());
            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    User updatedUser = mapRowToUser(rs);
                    return Optional.of(updatedUser);
                } else {
                    return Optional.empty();
                }
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
    public void deleteUser(Long id) throws UserNotFoundException {
        String sql = "DELETE FROM coworking_service.users WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

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
        try (Connection connection = dataSource.getConnection();
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

