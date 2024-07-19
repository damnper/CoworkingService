package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.AdminOrOwner;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.AddResourceRequestDTO;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.dto.UpdateResourceRequestDTO;
import ru.y_lab.enums.ResourceType;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.ResourceMapper;
import ru.y_lab.model.Resource;
import ru.y_lab.repo.ResourceRepo;
import ru.y_lab.service.JWTService;
import ru.y_lab.service.ResourceService;

import java.util.List;

/**
 * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
 * It interacts with the ResourceRepository to perform CRUD operations.
 */
@Loggable
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMapper resourceMapper;
    private final ResourceRepo resourceRepo;
    private final JWTService jwsService;

    /**
     * Adds a new resource to the system.
     *
     * @param request the request containing resource details
     * @return the added resource as a ResourceDTO
     */
    @Override
    public ResourceDTO addResource(String token, AddResourceRequestDTO request, ResourceType resourceType) {
        Resource resource = createResource(request, resourceType, token);
        Resource savedResource = resourceRepo.save(resource);
        return resourceMapper.toDTO(savedResource);
    }

    /**
     * Retrieves a resource by its ID.
     *
     * @param resourceId the ID of the resource
     * @return the resource with owner details as a ResourceWithOwnerDTO
     * @throws ResourceNotFoundException if the resource is not found
     * @throws UserNotFoundException if the user who owns the resource is not found
     */
    @Override
    public ResourceWithOwnerDTO getResourceById(Long resourceId) {
        return resourceRepo.findResourceWithOwnerById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The requested resource could not be found. Please check the ID and try again."));
    }

    /**
     * Retrieves all resources in the system.
     *
     * @return a list of all resources with their owner details
     * @throws ResourceNotFoundException if no resources are found
     */
    @Override
    public List<ResourceWithOwnerDTO> getAllResources() {
        List<ResourceWithOwnerDTO> resources =  resourceRepo.findAllResourcesWithOwners();
        if (resources.isEmpty())
            throw new ResourceNotFoundException("No resources found in the system.");
        return resources;
    }

    /**
     * Updates an existing resource.
     *
     * @param resourceId the ID of the resource to be updated
     * @param request the update request containing updated resource details
     * @return the updated resource as a ResourceDTO
     * @throws ResourceNotFoundException if the resource is not found
     * @throws SecurityException if the user is not authorized to update the resource
     */
    @Override
    @AdminOrOwner
    public ResourceDTO updateResource(String token, Long resourceId, UpdateResourceRequestDTO request, ResourceType resourceType) {

        Resource resource = resourceRepo.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The resource you are trying to update could not be found. Please check the ID and try again."));

        resource.setName(request.resourceName());
        resource.setType(resourceType.name());

        Resource updatedResource = resourceRepo.save(resource);
        return resourceMapper.toDTO(updatedResource);
    }

    /**
     * Deletes a resource by its ID.
     *
     * @param resourceId the ID of the resource to be deleted
     * @throws ResourceNotFoundException if the resource is not found
     * @throws SecurityException if the user is not authorized to delete the resource
     */
    @Override
    @AdminOrOwner
    public void deleteResource(String token, Long resourceId) {
        resourceRepo.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The resource you are trying to delete could not be found. Please check the ID and try again."));
        resourceRepo.deleteById(resourceId);
    }

    private Resource createResource(AddResourceRequestDTO request, ResourceType resourceType, String token) {
        Long userId = jwsService.extractUserId(token);
        return Resource.builder()
                .userId(userId)
                .name(request.resourceName())
                .type(resourceType.name())
                .build();
    }
}
