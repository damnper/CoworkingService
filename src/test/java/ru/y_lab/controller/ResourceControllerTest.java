package ru.y_lab.controller;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.y_lab.dto.AddResourceRequestDTO;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.dto.UpdateResourceRequestDTO;
import ru.y_lab.enums.ResourceType;
import ru.y_lab.exception.AuthorizationException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.service.ResourceService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("ResourceController Tests")
class ResourceControllerTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private ResourceController resourceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add Resource")
    void addResource() {
        AddResourceRequestDTO addResourceRequest = new AddResourceRequestDTO("ResourceName");
        ResourceType resourceType = ResourceType.CONFERENCE_ROOM;
        ResourceDTO resourceDTO = new ResourceDTO(1L, 1L, "ResourceName", resourceType.name());

        when(resourceService.addResource(addResourceRequest, resourceType, httpRequest)).thenReturn(resourceDTO);

        ResponseEntity<ResourceDTO> response = resourceController.addResource(addResourceRequest, resourceType, httpRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(resourceDTO, response.getBody());
        verify(resourceService).addResource(addResourceRequest, resourceType, httpRequest);
    }

    @Test
    @DisplayName("Add Resource - Invalid Data")
    void addResourceInvalidData() {
        AddResourceRequestDTO addResourceRequest = new AddResourceRequestDTO("");
        ResourceType resourceType = ResourceType.CONFERENCE_ROOM;

        when(resourceService.addResource(addResourceRequest, resourceType, httpRequest)).thenThrow(new IllegalArgumentException("Invalid resource name"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> resourceController.addResource(addResourceRequest, resourceType, httpRequest));

        assertEquals("Invalid resource name", exception.getMessage());
        verify(resourceService).addResource(addResourceRequest, resourceType, httpRequest);
    }

    @Test
    @DisplayName("Get Resource By ID")
    void getResourceById() {
        Long resourceId = 1L;
        ResourceWithOwnerDTO resourceWithOwnerDTO = new ResourceWithOwnerDTO(1L, "ResourceName", "Type", 1L, "OwnerName");

        when(resourceService.getResourceById(resourceId, httpRequest)).thenReturn(resourceWithOwnerDTO);

        ResponseEntity<ResourceWithOwnerDTO> response = resourceController.getResourceById(resourceId, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resourceWithOwnerDTO, response.getBody());
        verify(resourceService).getResourceById(resourceId, httpRequest);
    }

    @Test
    @DisplayName("Get Resource By ID - Not Found")
    void getResourceByIdNotFound() {
        Long resourceId = 1L;

        when(resourceService.getResourceById(resourceId, httpRequest)).thenThrow(new ResourceNotFoundException("Resource not found"));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> resourceController.getResourceById(resourceId, httpRequest));

        assertEquals("Resource not found", exception.getMessage());
        verify(resourceService).getResourceById(resourceId, httpRequest);
    }

    @Test
    @DisplayName("Get All Resources")
    void getAllResources() {
        List<ResourceWithOwnerDTO> resourcesWithOwners = List.of(
                new ResourceWithOwnerDTO(1L, "Resource1", "Type1", 1L, "Owner1"),
                new ResourceWithOwnerDTO(2L, "Resource2", "Type2", 2L, "Owner2")
        );

        when(resourceService.getAllResources(httpRequest)).thenReturn(resourcesWithOwners);

        ResponseEntity<List<ResourceWithOwnerDTO>> response = resourceController.getAllResources(httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resourcesWithOwners, response.getBody());
        verify(resourceService).getAllResources(httpRequest);
    }

    @Test
    @DisplayName("Get All Resources - Unauthorized")
    void getAllResourcesUnauthorized() {
        when(resourceService.getAllResources(httpRequest)).thenThrow(new AuthorizationException("Not authorized"));

        Exception exception = assertThrows(AuthorizationException.class, () -> resourceController.getAllResources(httpRequest));

        assertEquals("Not authorized", exception.getMessage());
        verify(resourceService).getAllResources(httpRequest);
    }

    @Test
    @DisplayName("Update Resource")
    void updateResource() {
        Long resourceId = 1L;
        UpdateResourceRequestDTO updateRequest = new UpdateResourceRequestDTO("UpdatedResourceName");
        ResourceType resourceType = ResourceType.CONFERENCE_ROOM;
        ResourceDTO resourceDTO = new ResourceDTO(1L, 1L, "UpdatedResourceName", resourceType.name());

        when(resourceService.updateResource(resourceId, updateRequest, resourceType, httpRequest)).thenReturn(resourceDTO);

        ResponseEntity<ResourceDTO> response = resourceController.updateResource(resourceId, updateRequest, resourceType, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resourceDTO, response.getBody());
        verify(resourceService).updateResource(resourceId, updateRequest, resourceType, httpRequest);
    }

    @Test
    @DisplayName("Update Resource - Not Found")
    void updateResourceNotFound() {
        Long resourceId = 1L;
        UpdateResourceRequestDTO updateRequest = new UpdateResourceRequestDTO("UpdatedResourceName");
        ResourceType resourceType = ResourceType.CONFERENCE_ROOM;

        when(resourceService.updateResource(resourceId, updateRequest, resourceType, httpRequest)).thenThrow(new ResourceNotFoundException("Resource not found"));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> resourceController.updateResource(resourceId, updateRequest, resourceType, httpRequest));

        assertEquals("Resource not found", exception.getMessage());
        verify(resourceService).updateResource(resourceId, updateRequest, resourceType, httpRequest);
    }

    @Test
    @DisplayName("Delete Resource")
    void deleteResource() {
        Long resourceId = 1L;

        doNothing().when(resourceService).deleteResource(resourceId, httpRequest);

        ResponseEntity<Void> response = resourceController.deleteResource(resourceId, httpRequest);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(resourceService).deleteResource(resourceId, httpRequest);
    }

    @Test
    @DisplayName("Delete Resource - Not Found")
    void deleteResourceNotFound() {
        Long resourceId = 1L;

        doThrow(new ResourceNotFoundException("Resource not found")).when(resourceService).deleteResource(resourceId, httpRequest);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> resourceController.deleteResource(resourceId, httpRequest));

        assertEquals("Resource not found", exception.getMessage());
        verify(resourceService).deleteResource(resourceId, httpRequest);
    }

    @Test
    @DisplayName("Delete Resource - Unauthorized")
    void deleteResourceUnauthorized() {
        Long resourceId = 1L;

        doThrow(new AuthorizationException("Not authorized")).when(resourceService).deleteResource(resourceId, httpRequest);

        Exception exception = assertThrows(AuthorizationException.class, () -> resourceController.deleteResource(resourceId, httpRequest));

        assertEquals("Not authorized", exception.getMessage());
        verify(resourceService).deleteResource(resourceId, httpRequest);
    }
}
