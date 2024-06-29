package ru.y_lab.repo;

import ru.y_lab.config.DatabaseManager;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.model.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The ResourceRepository class provides methods to manage resources.
 * It stores resource data in a HashMap and supports CRUD operations.
 */
public class ResourceRepository {

    /**
     * Adds a new resource to the repository.
     * @param resource the resource to be added
     */
    public void addResource(Resource resource) {
        String sql = "INSERT INTO coworking_service.resources (id, user_id, name, type) VALUES (DEFAULT, (?), (?), (?))";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, resource.getUserId());
            ps.setString(2, resource.getName());
            ps.setString(3, resource.getType());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating resource failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    resource.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating resource failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding resource", e);
        }
    }

    /**
     * Retrieves a resource by its ID.
     * @param id the ID of the resource
     * @return the resource with the specified ID
     */
    public Optional<Resource> getResourceById(Long id) {
        String sql = "SELECT * FROM coworking_service.resources WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return Optional.ofNullable(rs.next() ? mapRowToResource(rs) : null);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving resource by ID", e);
        }
    }

    /**
     * Updates an existing resource in the repository.
     * @param resource the resource with updated information
     * @throws ResourceNotFoundException if the resource with the specified ID is not found
     */
    public void updateResource(Resource resource) throws ResourceNotFoundException {
        String sql = "UPDATE coworking_service.resources " +
                     "SET user_id = ?, name = ?, type = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, resource.getUserId());
            ps.setString(2, resource.getName());
            ps.setString(3, resource.getType());
            ps.setLong(4, resource.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("Resource with ID " + resource.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating resource", e);
        }
    }

    /**
     * Deletes a resource from the repository by its ID.
     * @param id the ID of the resource to be deleted
     * @throws ResourceNotFoundException if the resource with the specified ID is not found
     */
    public void deleteResource(Long id) throws ResourceNotFoundException {
        String sql = "DELETE FROM coworking_service.resources WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("Resource with ID " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting resource", e);
        }
    }

    /**
     * Retrieves all resources from the repository.
     * @return a list of all resources
     */
    public List<Resource> getAllResources() {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT * FROM coworking_service.resources";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Resource resource = mapRowToResource(rs);
                resources.add(resource);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving all resources", e);
        }
        return resources;
    }

    /**
     * Maps a row from a ResultSet to a Resource object.
     * @param rs the ResultSet containing the data to map
     * @return a Resource object mapped from the ResultSet data
     * @throws SQLException if there is an error accessing the ResultSet
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

