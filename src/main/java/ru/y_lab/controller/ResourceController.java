package ru.y_lab.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.AddResourceRequestDTO;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.dto.UpdateResourceRequestDTO;
import ru.y_lab.enums.ResourceType;
import ru.y_lab.service.ResourceService;
import ru.y_lab.swagger.API.ResourceControllerAPI;

import java.util.List;

/**
 * Controller for managing resources.
 * This class handles HTTP requests for creating, retrieving, updating, and deleting resources.
 */
@Tag(name = "Resource API", description = "Operations about resources")
@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController implements ResourceControllerAPI {

    private final ResourceService resourceService;

    /**
     * Adds a new resource.
     *
     * @param token the authentication token of the user making the request
     * @param addResourceRequest the request containing resource details
     * @param resourceType the type of the resource
     * @return a {@link ResponseEntity} containing the added resource as a {@link ResourceDTO} with HTTP status CREATED
     */
    @Override
    @PostMapping
    public ResponseEntity<ResourceDTO> addResource(@RequestHeader("Authorization") String token,
                                                   @RequestBody AddResourceRequestDTO addResourceRequest,
                                                   @RequestParam ResourceType resourceType) {
        ResourceDTO resourceDTO = resourceService.addResource(token, addResourceRequest, resourceType);
        return new ResponseEntity<>(resourceDTO, HttpStatus.CREATED);
    }

    /**
     * Retrieves a resource by its ID.
     *
     * @param token the authentication token of the user making the request
     * @param resourceId the ID of the resource
     * @return a {@link ResponseEntity} containing the resource with owner details as a {@link ResourceWithOwnerDTO} with HTTP status OK
     */
    @Override
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceWithOwnerDTO> getResourceById(@RequestHeader("Authorization") String token,
                                                                @PathVariable("resourceId") Long resourceId) {
        ResourceWithOwnerDTO resourceWithOwnerDTO = resourceService.getResourceById(resourceId);
        return new ResponseEntity<>(resourceWithOwnerDTO, HttpStatus.OK);
    }

    /**
     * Retrieves all resources in the system.
     *
     * @param token the authentication token of the user making the request
     * @return a {@link ResponseEntity} containing a list of all resources with their owner details as {@link ResourceWithOwnerDTO} with HTTP status OK
     */
    @Override
    @GetMapping
    public ResponseEntity<List<ResourceWithOwnerDTO>> getAllResources(@RequestHeader("Authorization") String token) {
        List<ResourceWithOwnerDTO> resourcesWithOwners = resourceService.getAllResources();
        return new ResponseEntity<>(resourcesWithOwners, HttpStatus.OK);
    }

    /**
     * Updates an existing resource.
     *
     * @param token the authentication token of the user making the request
     * @param resourceId the ID of the resource to be updated
     * @param request the update request containing updated resource details
     * @param resourceType the type of the resource
     * @return a {@link ResponseEntity} containing the updated resource as a {@link ResourceDTO} with HTTP status OK
     */
    @Override
    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceDTO> updateResource(@RequestHeader("Authorization") String token,
                                                      @PathVariable("resourceId") Long resourceId,
                                                      @RequestBody UpdateResourceRequestDTO request,
                                                      @RequestParam ResourceType resourceType) {
        ResourceDTO resourceDTO = resourceService.updateResource(token, resourceId, request, resourceType);
        return new ResponseEntity<>(resourceDTO, HttpStatus.OK);
    }

    /**
     * Deletes a resource by its ID.
     *
     * @param token the authentication token of the user making the request
     * @param resourceId the ID of the resource to be deleted
     * @return a {@link ResponseEntity} with HTTP status NO_CONTENT
     */
    @Override
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@RequestHeader("Authorization") String token,
                                               @PathVariable("resourceId") Long resourceId) {
        resourceService.deleteResource(token, resourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
