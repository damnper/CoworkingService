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
import ru.y_lab.ui.ResourceUI;
import ru.y_lab.util.InputReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.y_lab.constants.ColorTextCodes.*;

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

    @Mock
    private ResourceUI resourceUI;

    private ResourceServiceImpl resourceService;

    /**
     * Sets up the test environment by initializing mocks and creating an instance of {@link ResourceServiceImpl}.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceService = new ResourceServiceImpl(inputReader, resourceRepository, userService, resourceUI);
    }

    /**
     * Test case for {@link ResourceServiceImpl#getResourceById(Long)} when the resource exists.
     */
    @Test
    public void testGetResourceById_ResourceExists() {
        Resource mockResource = new Resource(1L, 2L, "resourceName", "Workspace");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(mockResource));

        Resource resource = resourceService.getResourceById(1L);

        assertNotNull(resource);
        assertEquals(1L, resource.getId());
        assertEquals(2L, resource.getUserId());
        assertEquals("resourceName", resource.getName());
        assertEquals("Workspace", resource.getType());
    }

    /**
     * Test case for {@link ResourceServiceImpl#getResourceById(Long)} when the resource does not exist.
     */
    @Test
    public void testGetResourceById_ResourceNotFound() {
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById(1L));
    }

    /**
     * Test case for {@link ResourceServiceImpl#manageResources()} when the user is not logged in.
     */
    @Test
    public void testManageResources_UserNotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        resourceService.manageResources();

        verify(userService, times(1)).getCurrentUser();
        verifyNoMoreInteractions(resourceUI);
    }

    /**
     * Test case for {@link ResourceServiceImpl#manageResources()} when the user is logged in.
     * Verifies the management flow with valid choices.
     */
    @Test
    public void testManageResources_UserLoggedIn() {
        User mockUser = new User(1L, "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(inputReader.getUserChoice()).thenReturn(0);

        resourceService.manageResources();

        verify(resourceUI, times(1)).showResourceMenu(userService);
        verifyNoMoreInteractions(resourceUI);
    }

    /**
     * Test case for {@link ResourceServiceImpl#viewResources()} when no resources are available.
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testViewResources_NoResourcesAvailable() throws UserNotFoundException {
        when(resourceRepository.getAllResources()).thenReturn(Collections.emptyList());

        resourceService.viewResources();

        verify(resourceRepository, times(1)).getAllResources();
        verify(resourceUI, times(0)).showAvailableResources(any(), any()); // No resources to show
    }

    /**
     * Test case for {@link ResourceServiceImpl#viewResources()} when resources are available.
     * @throws UserNotFoundException if the user is not found
     */
    @Test
    public void testViewResources_WithResources() throws UserNotFoundException {
        Resource mockResource = new Resource(1L, 2L, "resourceName", "Workspace");
        List<Resource> resources = List.of(mockResource);
        when(resourceRepository.getAllResources()).thenReturn(resources);
        User mockUser = new User(1L, "username", "password", "USER");
        when(userService.getUserById(2L)).thenReturn(mockUser);

        resourceService.viewResources();

        verify(resourceRepository, times(1)).getAllResources();
        verify(resourceUI, times(1)).showAvailableResources(resources, userService);
    }

    /**
     * Test case for {@link ResourceServiceImpl#addResource()}.
     */
    @Test
    public void testAddResource() {
        when(inputReader.readLine()).thenReturn("New Resource", "Workspace");
        User mockUser = new User(1L, "username", "password", "USER");
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
        Resource mockResource = new Resource(1L, 1L, "resourceName", "Workspace");
        when(inputReader.readLine()).thenReturn("1", "Updated Resource", "Conference Room");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(mockResource));
        User mockUser = new User(1L, "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(mockUser);

        resourceService.updateResource();

        verify(resourceRepository, times(1)).updateResource(any(Resource.class));
    }

    /**
     * Test case for {@link ResourceServiceImpl#updateResource()} when the resource does not exist.
     */
    @Test
    public void testUpdateResource_ResourceNotFound() throws ResourceNotFoundException {
        when(inputReader.readLine()).thenReturn("1");  // Simulate user input
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());  // Simulate resource not found

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outContent));

        ResourceNotFoundException thrownException = assertThrows(ResourceNotFoundException.class, () -> resourceService.updateResource());

        assertEquals(YELLOW + "Resource with ID " + 1L + " not found" + RESET, thrownException.getMessage());

        String expectedOutput = CYAN + "Enter resource ID: " + RESET;
        assertEquals(expectedOutput, outContent.toString());

        System.setOut(originalSystemOut);

        verify(resourceRepository, times(1)).getResourceById(1L);
        verify(resourceRepository, never()).updateResource(any(Resource.class));
    }

    /**
     * Test case for {@link ResourceServiceImpl#deleteResources()} when the resource exists.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testDeleteResources_ResourceExists() throws ResourceNotFoundException {
        Resource mockResource = new Resource(1L, 1L, "resourceName", "Workspace");
        when(inputReader.readLine()).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(mockResource));
        User mockUser = new User(1L, "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(mockUser);
        doNothing().when(resourceRepository).deleteResource(1L);

        resourceService.deleteResources();

        verify(resourceRepository, times(1)).deleteResource(1L);
    }

    /**
     * Test case for {@link ResourceServiceImpl#deleteResources()} when the resource does not exist.
     */
    @Test
    public void testDeleteResources_ResourceNotFound() {
        when(inputReader.readLine()).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        resourceService.deleteResources();

        verify(resourceRepository, times(1)).getResourceById(1L);
        verifyNoMoreInteractions(resourceRepository);
    }

    /**
     * Test case for {@link ResourceServiceImpl#manageResources()} when an invalid choice is made.
     */
    @Test
    public void testManageResources_InvalidChoice() {
        User mockUser = new User(1L, "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(inputReader.getUserChoice()).thenReturn(99, 0);  // Invalid choice

        resourceService.manageResources();

        verify(resourceUI, times(2)).showResourceMenu(userService);
    }

    /**
     * Test case for {@link ResourceServiceImpl#updateResource()} when the user tries to update a resource they do not own.
     */
    @Test
    public void testUpdateResource_NoAccess() throws Exception {
        Resource mockResource = new Resource(1L, 2L, "resourceName", "Workspace");
        when(inputReader.readLine()).thenReturn("1", "newName", "newType");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(mockResource));
        User mockUser = new User(1L, "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(mockUser);

        resourceService.updateResource();

        verify(resourceRepository, times(0)).updateResource(any(Resource.class));
    }

    /**
     * Test case for {@link ResourceServiceImpl#deleteResources()} when the user tries to delete a resource they do not own.
     */
    @Test
    public void testDeleteResources_NoAccess() throws Exception {
        Resource mockResource = new Resource(1L, 2L, "resourceName", "Workspace");
        when(inputReader.readLine()).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(mockResource));
        User mockUser = new User(1L, "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(mockUser);

        resourceService.deleteResources();

        verify(resourceRepository, times(0)).deleteResource(anyLong());
    }
}
