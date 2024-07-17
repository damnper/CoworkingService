package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.*;
import ru.y_lab.enums.ResourceType;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.ResourceMapper;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.service.ResourceService;
import ru.y_lab.util.AuthenticationUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.y_lab.enums.RoleType.USER;
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
    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    /**
     * Adds a new resource to the system.
     *
     * @param request the request containing resource details
     * @param httpRequest the HTTP request to get the session for authentication
     * @return the added resource as a ResourceDTO
     */
    @Override
    public ResourceDTO addResource(AddResourceRequestDTO request, ResourceType resourceType, HttpServletRequest httpRequest) {
        validateAddResourceRequest(request);
        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        Resource resource = Resource.builder()
                .userId(currentUser.id())
                .name(request.name())
                .type(resourceType.name())
                .build();
        Resource savedResource = resourceRepository.addResource(resource);
        return resourceMapper.toDTO(savedResource);
    }

    /**
     * Retrieves a resource by its ID.
     *
     * @param resourceId the ID of the resource
     * @param httpRequest the HTTP request to get the session for authentication
     * @return the resource with owner details as a ResourceWithOwnerDTO
     * @throws ResourceNotFoundException if the resource is not found
     * @throws UserNotFoundException if the user who owns the resource is not found
     */
    @Override
    public ResourceWithOwnerDTO getResourceById(Long resourceId, HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, USER.name());
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The requested resource could not be found. Please check the ID and try again."));
        User user = userRepository.getUserById(resource.getUserId())
                .orElseThrow(() -> new UserNotFoundException("The owner of this resource could not be found. Please try again later."));
        return resourceMapper.toResourceWithOwnerDTO(resource, user);
    }

    /**
     * Retrieves all resources in the system.
     *
     * @param httpRequest the HTTP request to get the session for authentication
     * @return a list of all resources with their owner details
     * @throws ResourceNotFoundException if no resources are found
     */
    @Override
    public List<ResourceWithOwnerDTO> getAllResources(HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, USER.name());
        List<Resource> resources = resourceRepository.getAllResources()
                .orElseThrow(() -> new ResourceNotFoundException("No resources found in the system."));
        return resources.stream()
                .map(resource -> {
                    User user = userRepository.getUserById(resource.getUserId())
                            .orElseThrow(() -> new UserNotFoundException("The owner of one or more resources could not be found. Please try again later."));
                    return resourceMapper.toResourceWithOwnerDTO(resource, user);
                })
                .toList();
    }

    /**
     * Updates an existing resource.
     *
     * @param resourceId the ID of the resource to be updated
     * @param request the update request containing updated resource details
     * @param httpRequest the HTTP request to get the session for authentication
     * @return the updated resource as a ResourceDTO
     * @throws ResourceNotFoundException if the resource is not found
     * @throws SecurityException if the user is not authorized to update the resource
     */
    @Override
    public ResourceDTO updateResource(Long resourceId, UpdateResourceRequestDTO request, ResourceType resourceType, HttpServletRequest httpRequest) {
        validateUpdateResourceRequest(request);

        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The resource you are trying to update could not be found. Please check the ID and try again."));
        authUtil.authorize(currentUser, resource.getUserId());

        resource.setName(request.name());
        resource.setType(resourceType.name());

        Resource updatedResource = resourceRepository.updateResource(resource);
        return resourceMapper.toDTO(updatedResource);
    }

    /**
     * Deletes a resource by its ID.
     *
     * @param resourceId the ID of the resource to be deleted
     * @param httpRequest the HTTP request to get the session for authentication
     * @throws ResourceNotFoundException if the resource is not found
     * @throws SecurityException if the user is not authorized to delete the resource
     */
    @Override
    public void deleteResource(Long resourceId, HttpServletRequest httpRequest) {
        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The resource you are trying to delete could not be found. Please check the ID and try again."));
        authUtil.authorize(currentUser, resource.getUserId());
        resourceRepository.deleteResource(resourceId);
    }
}
