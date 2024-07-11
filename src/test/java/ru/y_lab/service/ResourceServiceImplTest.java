package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.dto.AddResourceRequestDTO;
import ru.y_lab.dto.ResourceDTO;
import ru.y_lab.dto.ResourceWithOwnerDTO;
import ru.y_lab.dto.UserDTO;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomResourceMapper;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.impl.ResourceServiceImpl;
import ru.y_lab.util.AuthenticationUtil;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ResourceServiceImpl Tests - addResource")
public class ResourceServiceImplTest {

    @Mock
    private AuthenticationUtil authUtil;

    @Mock
    private CustomResourceMapper resourceMapper;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);
        when(req.getSession()).thenReturn(session);
    }

    /**
     * Test for successfully adding a resource.
     * This test verifies that a resource is successfully added and a success response is sent.
     */
    @Test
    @DisplayName("Add Resource")
    void addResource() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        AddResourceRequestDTO requestDTO = new AddResourceRequestDTO("ResourceName", "ResourceType");
        Resource resource = Resource.builder().userId(currentUser.id()).name(requestDTO.name()).type(requestDTO.type()).build();
        ResourceDTO resourceDTO = new ResourceDTO(1L, currentUser.id(), requestDTO.name(), requestDTO.type());

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"name\": \"ResourceName\", \"type\": \"ResourceType\"}")));
        when(resourceRepository.addResource(any(Resource.class))).thenReturn(resource);
        when(resourceMapper.toDTO(any(Resource.class))).thenReturn(resourceDTO);

        resourceService.addResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(201, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"userId\":1,\"name\":\"ResourceName\",\"type\":\"ResourceType\"}", stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access Add Resource")
    void addResource_unauthorized() throws IOException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new SecurityException("Access denied"));

        resourceService.addResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for invalid resource request.
     * This test verifies that an error response is sent when the resource data is invalid.
     */
    @Test
    @DisplayName("Invalid Resource Request")
    void addResource_invalidRequest() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"name\": \"\", \"type\": \"\"}")));

        resourceService.addResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(400, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Invalid name. Name must be 1 to 50 characters long and can only contain letters (Latin and Cyrillic).\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling general exceptions.
     * This test verifies that an error response is sent when a general exception occurs.
     */
    @Test
    @DisplayName("Handle General Exception Add Resource")
    void addResource_generalException() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"name\": \"ResourceName\", \"type\": \"ResourceType\"}")));
        doThrow(new RuntimeException("Unexpected error")).when(resourceRepository).addResource(any(Resource.class));

        resourceService.addResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(500, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Internal server error\"}", stringWriter.toString().trim());
    }


    /**
     * Test for successfully retrieving a resource by ID.
     * This test verifies that a resource is successfully retrieved and a success response is sent.
     */
    @Test
    @DisplayName("Get Resource By ID")
    void getResourceById() throws IOException, ResourceNotFoundException, UserNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource = Resource.builder().id(1L).userId(1L).name("ResourceName").type("ResourceType").build();
        User user = User.builder().id(1L).username("admin").password("adminpass").role("ADMIN").build();
        ResourceWithOwnerDTO resourceWithOwnerDTO = new ResourceWithOwnerDTO(1L, "ResourceName", "ResourceType", 1L,"admin");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(resourceMapper.toResourceWithOwnerDTO(any(Resource.class), any(User.class))).thenReturn(resourceWithOwnerDTO);

        resourceService.getResourceById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"name\":\"ResourceName\",\"type\":\"ResourceType\",\"userId\":1,\"username\":\"admin\"}", stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access Get Resource")
    void getResourceById_unauthorized() throws IOException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new SecurityException("Access denied"));

        resourceService.getResourceById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for resource not found.
     * This test verifies that an error response is sent when the resource is not found.
     */
    @Test
    @DisplayName("Resource Not Found Get By ID")
    void getResourceById_resourceNotFound() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        resourceService.getResourceById(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    /**
     * Test for successfully retrieving all resources.
     * This test verifies that all resources are successfully retrieved and a success response is sent.
     */
    @Test
    @DisplayName("Get All Resources")
    void getAllResources() throws IOException, UserNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource1 = Resource.builder().id(1L).userId(1L).name("Resource1").type("Type1").build();
        Resource resource2 = Resource.builder().id(2L).userId(2L).name("Resource2").type("Type2").build();
        User user1 = User.builder().id(1L).username("admin1").password("adminpass1").role("ADMIN").build();
        User user2 = User.builder().id(2L).username("admin2").password("adminpass2").role("ADMIN").build();
        ResourceWithOwnerDTO resourceDTO1 = new ResourceWithOwnerDTO(1L, "Resource1", "Type1", 1L,"admin1");
        ResourceWithOwnerDTO resourceDTO2 = new ResourceWithOwnerDTO(2L, "Resource2", "Type2", 2L,"admin2");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(resourceRepository.getAllResources()).thenReturn(List.of(resource1, resource2));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(user2));
        when(resourceMapper.toResourceWithOwnerDTO(resource1, user1)).thenReturn(resourceDTO1);
        when(resourceMapper.toResourceWithOwnerDTO(resource2, user2)).thenReturn(resourceDTO2);

        resourceService.getAllResources(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("[{\"id\":1,\"name\":\"Resource1\",\"type\":\"Type1\",\"userId\":1,\"username\":\"admin1\"}," +
                "{\"id\":2,\"name\":\"Resource2\",\"type\":\"Type2\",\"userId\":2,\"username\":\"admin2\"}]",
                stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access Get All Resources")
    void getAllResources_unauthorized() throws IOException {
        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenThrow(new SecurityException("Access denied"));

        resourceService.getAllResources(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling general exceptions.
     * This test verifies that an error response is sent when a general exception occurs.
     */
    @Test
    @DisplayName("Handle General Exception Get Resource")
    void getAllResources_generalException() throws IOException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(resourceRepository.getAllResources()).thenThrow(new RuntimeException("Unexpected error"));

        resourceService.getAllResources(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(400, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unexpected error\"}", stringWriter.toString().trim());
    }

    /**
     * Test for successfully updating a resource.
     * This test verifies that a resource is successfully updated and a success response is sent.
     */
    @Test
    @DisplayName("Update Resource")
    void updateResource() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource = Resource.builder().id(1L).userId(1L).name("Resource1").type("Type1").build();
        Resource updatedResource = Resource.builder().id(1L).userId(1L).name("UpdatedResource").type("UpdatedType").build();
        ResourceDTO resourceDTO = new ResourceDTO(1L, 1L,"UpdatedResource", "UpdatedType");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(authUtil.isUserAuthorizedToAction(currentUser, 1L)).thenReturn(true);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"name\": \"UpdatedResource\", \"type\": \"UpdatedType\"}")));
        when(resourceRepository.updateResource(any(Resource.class))).thenReturn(updatedResource);
        when(resourceMapper.toDTO(any(Resource.class))).thenReturn(resourceDTO);

        resourceService.updateResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(200, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"id\":1,\"userId\":1,\"name\":\"UpdatedResource\",\"type\":\"UpdatedType\"}", stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access Update Resource")
    void updateResource_unauthorized() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource = Resource.builder().id(1L).userId(2L).name("Resource1").type("Type1").build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(false);

        resourceService.updateResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling resource not found.
     * This test verifies that an error response is sent when the resource is not found.
     */
    @Test
    @DisplayName("Resource Not Found Update By ID")
    void updateResource_resourceNotFound() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        resourceService.updateResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling general exceptions.
     * This test verifies that an error response is sent when a general exception occurs.
     */
    @Test
    @DisplayName("Handle General Exception Update Resource")
    void updateResource_generalException() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource = Resource.builder().id(1L).userId(1L).name("Resource1").type("Type1").build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(authUtil.isUserAuthorizedToAction(currentUser, 1L)).thenReturn(true);
        when(req.getReader()).thenReturn(new BufferedReader(new StringReader("{\"name\": \"UpdatedResource\", \"type\": \"UpdatedType\"}")));
        when(resourceRepository.updateResource(any(Resource.class))).thenThrow(new RuntimeException("Unexpected error"));

        resourceService.updateResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(500, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unexpected error\"}", stringWriter.toString().trim());
    }

    /**
     * Test for successfully deleting a resource.
     * This test verifies that a resource is successfully deleted and a success response is sent.
     */
    @Test
    @DisplayName("Delete Resource")
    void deleteResource() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource = Resource.builder().id(1L).userId(1L).name("Resource1").type("Type1").build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(authUtil.isUserAuthorizedToAction(currentUser, 1L)).thenReturn(true);

        resourceService.deleteResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(204, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("", stringWriter.toString().trim());
    }

    /**
     * Test for unauthorized access.
     * This test verifies that an error response is sent when the user is not authorized.
     */
    @Test
    @DisplayName("Unauthorized Access Delete Resource")
    void deleteResource_unauthorized() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource = Resource.builder().id(1L).userId(2L).name("Resource1").type("Type1").build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(authUtil.isUserAuthorizedToAction(currentUser, 2L)).thenReturn(false);

        resourceService.deleteResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(401, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Access denied\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling resource not found.
     * This test verifies that an error response is sent when the resource is not found.
     */
    @Test
    @DisplayName("Resource Not Found Delete By ID")
    void deleteResource_resourceNotFound() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());

        resourceService.deleteResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(404, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Resource not found by ID: 1\"}", stringWriter.toString().trim());
    }

    /**
     * Test for handling general exceptions.
     * This test verifies that an error response is sent when a general exception occurs.
     */
    @Test
    @DisplayName("Handle General Exception Delete Resource")
    void deleteResource_generalException() throws IOException, ResourceNotFoundException {
        UserDTO currentUser = new UserDTO(1L, "admin", "adminpass", "ADMIN");
        Resource resource = Resource.builder().id(1L).userId(1L).name("Resource1").type("Type1").build();

        when(authUtil.authenticateAndAuthorize(any(HttpServletRequest.class), any())).thenReturn(currentUser);
        when(req.getParameter("resourceId")).thenReturn("1");
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(authUtil.isUserAuthorizedToAction(currentUser, 1L)).thenReturn(true);
        doThrow(new RuntimeException("Unexpected error")).when(resourceRepository).deleteResource(1L);

        resourceService.deleteResource(req, resp);

        writer.flush();
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(resp).setStatus(statusCaptor.capture());
        assertEquals(500, statusCaptor.getValue());

        verify(resp).setContentType("application/json");
        assertEquals("{\"message\":\"Unexpected error\"}", stringWriter.toString().trim());
    }
}
