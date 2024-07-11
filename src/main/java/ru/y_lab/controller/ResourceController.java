package ru.y_lab.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.*;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.service.ResourceService;
import ru.y_lab.util.AuthenticationUtil;

import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final AuthenticationUtil authUtil;

    @PostMapping
    public ResponseEntity<ResourceDTO> addResource(HttpServletRequest req, @RequestBody AddResourceRequestDTO request) {
        UserDTO currentUser = authUtil.authenticate(req, null);
        ResourceDTO resourceDTO = resourceService.addResource(request, currentUser);
        return new ResponseEntity<>(resourceDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceWithOwnerDTO> getResourceById(HttpServletRequest req, @PathVariable("resourceId") Long resourceId) throws UserNotFoundException, ResourceNotFoundException {
        authUtil.authenticate(req, null);
        ResourceWithOwnerDTO resourceWithOwnerDTO = resourceService.getResourceById(resourceId);
        return new ResponseEntity<>(resourceWithOwnerDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ResourceWithOwnerDTO>> getAllResources(HttpServletRequest req) throws ResourceNotFoundException {
        authUtil.authenticate(req, null);
        List<ResourceWithOwnerDTO> resourcesWithOwners = resourceService.getAllResources();
        return new ResponseEntity<>(resourcesWithOwners, HttpStatus.OK);
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceDTO> updateResource(HttpServletRequest req, @PathVariable("resourceId") Long resourceId, @RequestBody UpdateResourceRequestDTO request) throws ResourceNotFoundException {
        UserDTO currentUser = authUtil.authenticate(req, null);
        ResourceDTO resourceDTO = resourceService.updateResource(resourceId, request, currentUser);
        return new ResponseEntity<>(resourceDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(HttpServletRequest req, @PathVariable("resourceId") Long resourceId) throws ResourceNotFoundException {
        UserDTO currentUser = authUtil.authenticate(req, null);
        resourceService.deleteResource(resourceId, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
