package ru.y_lab.service;

import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;

import java.util.List;

/**
 * The ResourceService interface defines methods for managing resources.
 */
public interface ResourceService {

    ResourceDTO addResource(String resourceJson);

    void updateResource(String resourceJson);

    /**
     * Displays a list of available resources.
     * @throws UserNotFoundException if the user is not found
     */
    List<ResourceDTO> viewResources() throws UserNotFoundException;

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param resourceId the ID of the resource to retrieve
     * @return the resource with the specified ID
     * @throws ResourceNotFoundException if the resource with the given ID is not found
     */
    ResourceDTO getResourceById(Long resourceId) throws ResourceNotFoundException;

    void deleteResource(Long resourceId);
}
