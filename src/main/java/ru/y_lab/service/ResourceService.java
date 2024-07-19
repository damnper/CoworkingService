package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.y_lab.dto.*;
import ru.y_lab.enums.ResourceType;

import java.util.List;

/**
 * The ResourceService interface defines methods for managing resources.
 */
public interface ResourceService {
    /**
     * Adds a new resource to the system.
     *
     * @param request the request containing resource details
     * @return the added resource as a ResourceDTO
     */
    ResourceDTO addResource(String token,
                            AddResourceRequestDTO request,
                            ResourceType resourceType);

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
     * @return the updated resource as a ResourceDTO
     * @throws SecurityException if the user is not authorized to update the resource
     */
    ResourceDTO updateResource(String token,
                               Long resourceId,
                               UpdateResourceRequestDTO request,
                               ResourceType resourceType);

    /**
     * Deletes a resource by its ID.
     *
     * @param resourceId the ID of the resource to be deleted
     * @throws SecurityException if the user is not authorized to delete the resource
     */
    void deleteResource(String token,
                        Long resourceId);
}
