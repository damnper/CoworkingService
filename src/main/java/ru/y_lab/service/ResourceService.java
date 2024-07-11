package ru.y_lab.service;

import ru.y_lab.dto.*;

import java.util.List;

/**
 * The ResourceService interface defines methods for managing resources.
 */
public interface ResourceService {

    /**
     * Adds a new resource to the system.
     *
     * @param request the request containing resource details
     * @param currentUser the current authenticated user
     * @return the added resource as a ResourceDTO
     */
    ResourceDTO addResource(AddResourceRequestDTO request, UserDTO currentUser);

    /**
     * Retrieves a resource by its ID.
     *
     * @param resourceId the ID of the resource
     * @return the resource with owner details as a ResourceWithOwnerDTO
     */
    ResourceWithOwnerDTO getResourceById(Long resourceId);

    /**
     * Retrieves all resources in the system.
     *
     * @return a list of all resources with their owner details
     */
    List<ResourceWithOwnerDTO> getAllResources();

    /**
     * Updates an existing resource.
     *
     * @param resourceId the ID of the resource to be updated
     * @param request the update request containing updated resource details
     * @param currentUser the current authenticated user
     * @return the updated resource as a ResourceDTO
     */
    ResourceDTO updateResource(Long resourceId, UpdateResourceRequestDTO request, UserDTO currentUser);

    /**
     * Deletes a resource by its ID.
     *
     * @param resourceId the ID of the resource to be deleted
     * @param currentUser the current authenticated user
     */
    void deleteResource(Long resourceId, UserDTO currentUser);
}
