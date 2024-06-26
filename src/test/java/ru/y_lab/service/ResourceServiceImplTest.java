package ru.y_lab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.service.impl.ResourceServiceImpl;
import ru.y_lab.util.InputReader;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ResourceServiceImpl}.
 */
@DisplayName("Unit Tests for ResourceServiceImplTest")
public class ResourceServiceImplTest {

    @Mock
    private InputReader inputReader;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserService userService;

    private ResourceServiceImpl resourceService;

    /**
     * Sets up the test environment by initializing mocks and creating an instance of {@link ResourceServiceImpl}.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceService = new ResourceServiceImpl(inputReader, resourceRepository, userService);
    }

    /**
     * Test case for {@link ResourceServiceImpl#getResourceById(String)} when the resource exists.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testGetResourceById_ResourceExists() throws ResourceNotFoundException {
        Resource mockResource = new Resource("1", "userId", "resourceName", "resourceType");
        when(resourceRepository.getResourceById("1")).thenReturn(mockResource);

        Resource resource = resourceService.getResourceById("1");

        assertNotNull(resource);
        assertEquals("1", resource.getId());
        assertEquals("userId", resource.getUserId());
        assertEquals("resourceName", resource.getName());
        assertEquals("resourceType", resource.getType());
    }

    /**
     * Test case for {@link ResourceServiceImpl#getResourceById(String)} when the resource does not exist.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testGetResourceById_ResourceNotFound() throws ResourceNotFoundException {
        when(resourceRepository.getResourceById("1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById("1"));
    }

    /**
     * Test case for {@link ResourceServiceImpl#manageResources()} when the user is not logged in.
     *
     * @throws UserNotFoundException if the user is not found
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testManageResources_UserNotLoggedIn() throws UserNotFoundException, ResourceNotFoundException {
        when(userService.getCurrentUser()).thenReturn(null);

        resourceService.manageResources();

        verify(userService, times(1)).getCurrentUser();
    }

    /**
     * Test case for {@link ResourceServiceImpl#viewResources()} when no resources are available.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testViewResources_NoResourcesAvailable() throws UserNotFoundException {
        when(resourceRepository.getAllResources()).thenReturn(new ArrayList<>());

        resourceService.viewResources();

        verify(resourceRepository, times(1)).getAllResources();
    }
    /**
     * Test case for {@link ResourceServiceImpl#viewResources()} when resources are available.
     *
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testViewResources_WithResources() throws UserNotFoundException {
        Resource mockResource = new Resource("1", "userId", "resourceName", "resourceType");
        List<Resource> resources = List.of(mockResource);
        when(resourceRepository.getAllResources()).thenReturn(resources);
        User mockUser = new User("userId", "username", "password", "USER");
        when(userService.getUserById("userId")).thenReturn(mockUser);

        resourceService.viewResources();

        verify(resourceRepository, times(1)).getAllResources();
        verify(userService, times(1)).getUserById("userId");
    }

    /**
     * Test case for {@link ResourceServiceImpl#addResource()}.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testAddResource() throws ResourceNotFoundException {
        when(inputReader.readLine()).thenReturn("resourceName", "Workspace");
        User mockUser = new User("userId", "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(mockUser);
        doNothing().when(resourceRepository).addResource(any(Resource.class));

        resourceService.addResource();

        verify(resourceRepository, times(1)).addResource(any(Resource.class));
    }

    /**
     * Test case for {@link ResourceServiceImpl#updateResource()} when the resource exists.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testUpdateResource_ResourceExists() throws ResourceNotFoundException {
        Resource mockResource = new Resource("1", "userId", "resourceName", "resourceType");
        when(inputReader.readLine()).thenReturn("1", "newResourceName", "newResourceType");
        when(resourceRepository.getResourceById("1")).thenReturn(mockResource);
        User mockUser = new User("userId", "Admin", "admin", "ADMIN");
        when(userService.getCurrentUser()).thenReturn(mockUser);

        resourceService.updateResource();

        verify(resourceRepository, times(1)).updateResource(any(Resource.class));
    }

    /**
     * Test case for {@link ResourceServiceImpl#updateResource()} when the resource does not exist.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testUpdateResource_ResourceNotFound() throws ResourceNotFoundException {
        when(inputReader.readLine()).thenReturn("1");
        when(resourceRepository.getResourceById("1")).thenReturn(null);

        resourceService.updateResource();

        verify(resourceRepository, times(1)).getResourceById("1");
    }

    /**
     * Test case for {@link ResourceServiceImpl#deleteResources()} when the resource exists.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testDeleteResources_ResourceExists() throws ResourceNotFoundException {
        Resource mockResource = new Resource("1", "userId", "resourceName", "resourceType");
        when(inputReader.readLine()).thenReturn("1");
        when(resourceRepository.getResourceById("1")).thenReturn(mockResource);
        User mockUser = new User("userId", "Admin", "admin", "ADMIN");
        when(userService.getCurrentUser()).thenReturn(mockUser);
        doNothing().when(resourceRepository).deleteResource("1");

        resourceService.deleteResources();

        verify(resourceRepository, times(1)).deleteResource("1");
    }

    /**
     * Test case for {@link ResourceServiceImpl#deleteResources()} when the resource does not exist.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testDeleteResources_ResourceNotFound() throws ResourceNotFoundException {
        when(inputReader.readLine()).thenReturn("1");
        when(resourceRepository.getResourceById("1")).thenReturn(null);

        resourceService.deleteResources();

        verify(resourceRepository, times(1)).getResourceById("1");
    }
}
