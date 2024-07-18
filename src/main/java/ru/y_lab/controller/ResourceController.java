package ru.y_lab.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * Adds a new resource.
     *
     * @param addResourceRequest the request containing resource details
     * @param resourceType the resourceType of the resource
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing the added resource as a {@link ResourceDTO} with HTTP status CREATED
     */
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
     * @param resourceId the ID of the resource
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing the resource with owner details as a {@link ResourceWithOwnerDTO} with HTTP status OK
     */
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceWithOwnerDTO> getResourceById(@PathVariable("resourceId") Long resourceId, HttpServletRequest httpRequest) {
        ResourceWithOwnerDTO resourceWithOwnerDTO = resourceService.getResourceById(resourceId, httpRequest);
        return new ResponseEntity<>(resourceWithOwnerDTO, HttpStatus.OK);
    }

    /**
     * Retrieves all resources in the system.
     *
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing a list of all resources with their owner details as {@link ResourceWithOwnerDTO} with HTTP status OK
     */
    @GetMapping
    public ResponseEntity<List<ResourceWithOwnerDTO>> getAllResources(HttpServletRequest httpRequest) {
        List<ResourceWithOwnerDTO> resourcesWithOwners = resourceService.getAllResources(httpRequest);
        return new ResponseEntity<>(resourcesWithOwners, HttpStatus.OK);
    }

    /**
     * Updates an existing resource.
     *
     * @param resourceId the ID of the resource to be updated
     * @param request the update request containing updated resource details
     * @param resourceType the resourceType of the resource
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} containing the updated resource as a {@link ResourceDTO} with HTTP status OK
     */
    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceDTO> updateResource(@PathVariable("resourceId") Long resourceId,
                                                      @RequestBody UpdateResourceRequestDTO request,
                                                      @RequestParam ResourceType resourceType,
                                                      HttpServletRequest httpRequest) {
        ResourceDTO resourceDTO = resourceService.updateResource(resourceId, request, resourceType, httpRequest);
        return new ResponseEntity<>(resourceDTO, HttpStatus.OK);
    }

    /**
     * Deletes a resource by its ID.
     *
     * @param resourceId the ID of the resource to be deleted
     * @param httpRequest the HTTP request for session authentication
     * @return a {@link ResponseEntity} with HTTP status NO_CONTENT
     */
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable("resourceId") Long resourceId, HttpServletRequest httpRequest) {
        resourceService.deleteResource(resourceId, httpRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
