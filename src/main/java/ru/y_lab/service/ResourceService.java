package ru.y_lab.service;

import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;

/**
 * The ResourceService interface defines methods for managing resources.
 */
public interface ResourceService {

    /**
     * Manages resources including CRUD operations.
     */
    void manageResources();

    /**
     * Displays a list of available resources.
     * @throws UserNotFoundException if the user is not found
     */
    void viewResources() throws UserNotFoundException;

    /**
     * Retrieves a resource by its unique identifier.
     * @param resourceId the ID of the resource to retrieve
     * @return the resource with the specified ID
     * @throws ResourceNotFoundException if the resource with the given ID is not found
     */
    Resource getResourceById(Long resourceId) throws ResourceNotFoundException;

}
