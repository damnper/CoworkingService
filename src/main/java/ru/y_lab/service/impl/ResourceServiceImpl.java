package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.*;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomResourceMapper;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.ResourceService;
import ru.y_lab.util.AuthenticationUtil;

import java.util.List;

import static ru.y_lab.util.ValidationUtil.validateAddResourceRequest;
import static ru.y_lab.util.ValidationUtil.validateUpdateResourceRequest;

/**
 * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
 * It interacts with the ResourceRepository to perform CRUD operations.
 */
@Loggable
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final AuthenticationUtil authUtil;
    private final CustomResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    /**
     * Adds a new resource to the system.
     *
     * @param request the request containing resource details
     * @param currentUser the current authenticated user
     * @return the added resource as a ResourceDTO
     */
    @Override
    public ResourceDTO addResource(AddResourceRequestDTO request, UserDTO currentUser) {
        validateAddResourceRequest(request);
        Resource resource = Resource.builder()
                .userId(currentUser.id())
                .name(request.name())
                .type(request.type())
                .build();
        Resource savedResource = resourceRepository.addResource(resource);
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
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + resourceId));
        User user = userRepository.getUserById(resource.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + resource.getUserId()));
        return resourceMapper.toResourceWithOwnerDTO(resource, user);
    }

    /**
     * Retrieves all resources in the system.
     *
     * @return a list of all resources with their owner details
     * @throws ResourceNotFoundException if no resources are found
     */
    @Override
    public List<ResourceWithOwnerDTO> getAllResources() throws ResourceNotFoundException {
        List<Resource> resources = resourceRepository.getAllResources()
                .orElseThrow(() -> new ResourceNotFoundException("No resources found"));
        return resources.stream()
                .map(resource -> {
                    try {
                        User user = userRepository.getUserById(resource.getUserId())
                                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + resource.getUserId()));
                        return resourceMapper.toResourceWithOwnerDTO(resource, user);
                    } catch (UserNotFoundException e) {
                        throw new RuntimeException("Error mapping resource to owner", e);
                    }
                })
                .toList();
    }

    /**
     * Updates an existing resource.
     *
     * @param resourceId the ID of the resource to be updated
     * @param request the update request containing updated resource details
     * @param currentUser the current authenticated user
     * @return the updated resource as a ResourceDTO
     * @throws ResourceNotFoundException if the resource is not found
     * @throws SecurityException if the user is not authorized to update the resource
     */
    @Override
    public ResourceDTO updateResource(Long resourceId, UpdateResourceRequestDTO request, UserDTO currentUser) throws ResourceNotFoundException {
        validateUpdateResourceRequest(request);
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + resourceId));
        authUtil.authorize(currentUser, resource.getUserId());
        resource.setName(request.name());
        resource.setType(request.type());

        Resource updatedResource = resourceRepository.updateResource(resource);
        return resourceMapper.toDTO(updatedResource);
    }

    /**
     * Deletes a resource by its ID.
     *
     * @param resourceId the ID of the resource to be deleted
     * @param currentUser the current authenticated user
     * @throws ResourceNotFoundException if the resource is not found
     * @throws SecurityException if the user is not authorized to delete the resource
     */
    @Override
    public void deleteResource(Long resourceId, UserDTO currentUser) throws SecurityException, ResourceNotFoundException {
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + resourceId));
        authUtil.authorize(currentUser, resource.getUserId());
        resourceRepository.deleteResource(resourceId);
    }


}
