package ru.y_lab.repo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.y_lab.config.DatabaseManager;
import ru.y_lab.exception.DatabaseException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.model.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The ResourceRepository class provides methods to manage resources.
 * It interacts with the database to perform CRUD operations for resources.
 */
@Repository
@AllArgsConstructor
public class ResourceRepository {

    private DatabaseManager databaseManager;

    // SQL Queries as constants
    private static final String ADD_RESOURCE_SQL = "INSERT INTO coworking_service.resources (id, user_id, name, type) VALUES (DEFAULT, ?, ?, ?)";
    private static final String GET_RESOURCE_BY_ID_SQL = "SELECT * FROM coworking_service.resources WHERE id = ?";
    private static final String GET_ALL_RESOURCES_SQL = "SELECT * FROM coworking_service.resources";
    private static final String UPDATE_RESOURCE_SQL = "UPDATE coworking_service.resources SET user_id = ?, name = ?, type = ? WHERE id = ?";
    private static final String DELETE_RESOURCE_SQL = "DELETE FROM coworking_service.resources WHERE id = ?";

    /**
     * Adds a new resource to the repository.
     * @param resource the resource to be added
     * @return the resource with the specified ID
     */
    public Resource addResource(Resource resource) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(ADD_RESOURCE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, resource.getUserId());
            ps.setString(2, resource.getName());
            ps.setString(3, resource.getType());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Resource creation failed. No rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    resource.setId(generatedKeys.getLong(1));
                    return resource;
                } else {
                    throw new DatabaseException("Resource creation failed. No ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while adding the resource. Please try again later." + e.getMessage());
        }
    }

    /**
     * Retrieves a resource by its ID.
     * @param id the ID of the resource
     * @return the resource with the specified ID
     */
    public Optional<Resource> getResourceById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_RESOURCE_BY_ID_SQL)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Resource resource = mapRowToResource(rs);
                    return Optional.of(resource);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving the resource by ID. Please try again later.");
        }
    }

    /**
     * Retrieves all resources from the repository.
     * @return a list of all resources
     */
    public Optional<List<Resource>> getAllResources() {
        List<Resource> resources = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(GET_ALL_RESOURCES_SQL)) {

            while (rs.next()) {
                Resource resource = mapRowToResource(rs);
                resources.add(resource);
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving all resources. Please try again later.");
        }
        return resources.isEmpty() ? Optional.empty() : Optional.of(resources);
    }

    /**
     * Updates an existing resource in the repository.
     * @param resource the resource with updated information
     * @return the updated resource
     */
    public Resource updateResource(Resource resource) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement updatePs = connection.prepareStatement(UPDATE_RESOURCE_SQL);
             PreparedStatement selectPs = connection.prepareStatement(GET_RESOURCE_BY_ID_SQL)) {

            // Check if resource exists
            selectPs.setLong(1, resource.getId());
            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    throw new ResourceNotFoundException("Resource with ID " + resource.getId() + " not found.");
                }
            }

            updatePs.setLong(1, resource.getUserId());
            updatePs.setString(2, resource.getName());
            updatePs.setString(3, resource.getType());
            updatePs.setLong(4, resource.getId());

            updatePs.executeUpdate();

            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    return mapRowToResource(rs);
                } else {
                    throw new ResourceNotFoundException("Resource with ID " + resource.getId() + " not found after update.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while updating the resource. Please try again later.");
        }
    }

    /**
     * Deletes a resource from the repository by its ID.
     * @param id the ID of the resource to be deleted
     * @throws ResourceNotFoundException if the resource with the specified ID is not found
     */
    public void deleteResource(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_RESOURCE_SQL)) {

            ps.setLong(1, id);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("Resource with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while deleting the resource. Please try again later.");
        }
    }

    /**
     * Maps a row from the ResultSet to a Resource object.
     * @param rs the ResultSet containing resource data
     * @return a Resource object mapped from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Resource mapRowToResource(ResultSet rs) throws SQLException {
        return Resource.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .type(rs.getString("type"))
                .build();
    }
}

