package ru.y_lab.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static ru.y_lab.util.RequestUtil.*;
import static ru.y_lab.util.ResponseUtil.sendErrorResponse;
import static ru.y_lab.util.ResponseUtil.sendSuccessResponse;

/**
 * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
 * It interacts with the ResourceRepository to perform CRUD operations.
 */
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final AuthenticationUtil authUtil = new AuthenticationUtil();
    private final CustomResourceMapper resourceMapper = new CustomResourceMapper();
    private final ResourceRepository resourceRepository = new ResourceRepository();
    private final UserRepository userRepository = new UserRepository();


    /**
     * Adds a new resource.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void addResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            String requestBody = getRequestBody(req);
            AddResourceRequestDTO addResourceRequest = parseRequest(requestBody, AddResourceRequestDTO.class);
            ResourceDTO resourceDTO = processAddResource(addResourceRequest, currentUser);
            sendSuccessResponse(resp, 201, resourceDTO);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getResourceById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, null);
            Long resourceId = Long.valueOf(req.getParameter("resourceId"));
            Resource resource = resourceRepository.getResourceById(resourceId).orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + resourceId));
            User user = userRepository.getUserById(resource.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found by ID: " + resource.getUserId()));
            ResourceWithOwnerDTO resourceWithOwnerDTO = resourceMapper.toResourceWithOwnerDTO(resource, user);
            sendSuccessResponse(resp, HttpServletResponse.SC_OK, resourceWithOwnerDTO);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (ResourceNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves a list of all resources.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getAllResources(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, null);
            List<Resource> allResources = resourceRepository.getAllResources();
            List<ResourceWithOwnerDTO> resourcesWithOwners = allResources.stream()
                    .map(resource -> {
                        try {
                            User user = userRepository.getUserById(resource.getUserId())
                                    .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + resource.getUserId()));
                            return resourceMapper.toResourceWithOwnerDTO(resource, user);
                        } catch (UserNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            sendSuccessResponse(resp, 200, resourcesWithOwners);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Updates an existing resource.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void updateResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            Long resourceIdToUpdate = Long.valueOf(req.getParameter("resourceId"));
            Resource resource = resourceRepository.getResourceById(resourceIdToUpdate).orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + resourceIdToUpdate));
            if (!authUtil.isUserAuthorizedToAction(currentUser, resource.getUserId())) throw new SecurityException("Access denied");

            String requestBody = getRequestBody(req);
            UpdateResourceRequestDTO updateRequest = parseRequest(requestBody, UpdateResourceRequestDTO.class);
            ResourceDTO resourceDTO = processUpdatingResource(resourceIdToUpdate, updateRequest, resource);
            sendSuccessResponse(resp, 200, resourceDTO);
        } catch (ResourceNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    /**
     * Deletes a resource.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void deleteResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            Long userIdToDelete = Long.valueOf(req.getParameter("resourceId"));
            Resource resource = resourceRepository.getResourceById(userIdToDelete).orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + userIdToDelete));
            if (!authUtil.isUserAuthorizedToAction(currentUser, resource.getUserId())) throw new SecurityException("Access denied");

            processResourceDeletion(userIdToDelete, currentUser, req);
            sendSuccessResponse(resp, 204);
        } catch (ResourceNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Adds a new resource to the repository.
     *
     * @param addResourceRequest the request object containing the details of the resource to be added
     * @param currentUser the current authenticated user
     * @return the added resource as a ResourceDTO
     */
    private ResourceDTO processAddResource(AddResourceRequestDTO addResourceRequest, UserDTO currentUser) {
        Resource resourceDTO = Resource.builder()
                .userId(currentUser.id())
                .name(addResourceRequest.name())
                .type(addResourceRequest.type())
                .build();
        Resource resource = resourceRepository.addResource(resourceDTO);
        return resourceMapper.toDTO(resource);
    }

    /**
     * Updates an existing resource in the repository.
     *
     * @param resourceIdToUpdate the ID of the resource to update
     * @param request the request object containing the updated details of the resource
     * @return the updated resource as a ResourceDTO
     * @throws ResourceNotFoundException if the resource with the given ID is not found
     */
    private ResourceDTO processUpdatingResource(Long resourceIdToUpdate, UpdateResourceRequestDTO request, Resource resource) throws ResourceNotFoundException {
        try {
            resource.setName(request.name());
            resource.setType(request.type());
            return resourceMapper.toDTO(resourceRepository.updateResource(resource));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found by ID: " + resourceIdToUpdate);
        }
    }

    /**
     * Deletes a resource from the repository.
     *
     * @param resourceIdToDelete the ID of the resource to delete
     * @param currentUser the current authenticated user
     * @param req the HttpServletRequest object for invalidating the session if needed
     * @throws ResourceNotFoundException if the resource with the given ID is not found
     */
    private void processResourceDeletion(Long resourceIdToDelete, UserDTO currentUser, HttpServletRequest req) throws ResourceNotFoundException {
        try {
            resourceRepository.deleteResource(resourceIdToDelete);
            if (currentUser.id().equals(resourceIdToDelete)) {
                req.getSession().invalidate();
            }
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found by ID: " + resourceIdToDelete);
        }
    }


}
