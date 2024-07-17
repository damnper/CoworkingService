package ru.y_lab.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.dto.*;
import ru.y_lab.enums.ResourceType;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.ResourceMapper;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.service.impl.ResourceServiceImpl;
import ru.y_lab.util.AuthenticationUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ResourceServiceImpl Tests - addResource")
public class ResourceServiceImplTest {

    @Mock
    private AuthenticationUtil authUtil;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ResourceServiceImpl resourceService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add Resource")
    void addResource() {
        AddResourceRequestDTO requestDTO = new AddResourceRequestDTO("ResourceName");
        ResourceType resourceType = ResourceType.CONFERENCE_ROOM;
        UserDTO currentUser = new UserDTO(1L, "user", "USER");
        Resource resource = Resource.builder()
                .userId(currentUser.id())
                .name(requestDTO.name())
                .type(resourceType.name())
                .build();
        Resource savedResource = Resource.builder()
                .id(1L)
                .userId(currentUser.id())
                .name(requestDTO.name())
                .type(resourceType.name())
                .build();
        ResourceDTO resourceDTO = new ResourceDTO(1L, currentUser.id(), requestDTO.name(), resourceType.name());

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.addResource(any(Resource.class))).thenReturn(savedResource);
        when(resourceMapper.toDTO(savedResource)).thenReturn(resourceDTO);
        when(req.getSession(false)).thenReturn(session);

        ResourceDTO result = resourceService.addResource(requestDTO, resourceType, req);

        assertEquals(resourceDTO, result);
        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).addResource(resource);
        verify(resourceMapper).toDTO(savedResource);
    }

    @Test
    @DisplayName("Get Resource By ID")
    void getResourceById() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");
        Resource resource = Resource.builder()
                .id(1L)
                .userId(1L)
                .name("ResourceName")
                .type("ResourceType")
                .build();
        User user = User.builder()
                .id(1L)
                .username("user")
                .password("password")
                .role("USER")
                .build();
        ResourceWithOwnerDTO resourceWithOwnerDTO = new ResourceWithOwnerDTO(1L, "ResourceName", "ResourceType", 1L, "user");

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(resourceMapper.toResourceWithOwnerDTO(resource, user)).thenReturn(resourceWithOwnerDTO);
        when(req.getSession(false)).thenReturn(session);

        ResourceWithOwnerDTO result = resourceService.getResourceById(1L, req);

        assertEquals(resourceWithOwnerDTO, result);
        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getResourceById(1L);
        verify(userRepository).getUserById(1L);
        verify(resourceMapper).toResourceWithOwnerDTO(resource, user);
    }

    @Test
    @DisplayName("Get Resource By ID - Resource Not Found")
    void getResourceById_ResourceNotFound() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());
        when(req.getSession(false)).thenReturn(session);

        assertThrows(ResourceNotFoundException.class, () -> resourceService.getResourceById(1L, req));

        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getResourceById(1L);
        verify(userRepository, never()).getUserById(anyLong());
        verify(resourceMapper, never()).toResourceWithOwnerDTO(any(Resource.class), any(User.class));
    }

    @Test
    @DisplayName("Get Resource By ID - User Not Found")
    void getResourceById_UserNotFound() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");
        Resource resource = Resource.builder()
                .id(1L)
                .userId(1L)
                .name("ResourceName")
                .type("ResourceType")
                .build();

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        when(userRepository.getUserById(1L)).thenReturn(Optional.empty());
        when(req.getSession(false)).thenReturn(session);

        assertThrows(UserNotFoundException.class, () -> resourceService.getResourceById(1L, req));

        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getResourceById(1L);
        verify(userRepository).getUserById(1L);
        verify(resourceMapper, never()).toResourceWithOwnerDTO(any(Resource.class), any(User.class));
    }

    @Test
    @DisplayName("Get All Resources")
    void getAllResources() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");
        Resource resource1 = Resource.builder()
                .id(1L)
                .userId(1L)
                .name("Resource1")
                .type("Type1")
                .build();
        Resource resource2 = Resource.builder()
                .id(2L)
                .userId(2L)
                .name("Resource2")
                .type("Type2")
                .build();
        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .password("password1")
                .role("USER")
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .password("password2")
                .role("USER")
                .build();
        ResourceWithOwnerDTO resourceDTO1 = new ResourceWithOwnerDTO(1L, "Resource1", "Type1", 1L, "user1");
        ResourceWithOwnerDTO resourceDTO2 = new ResourceWithOwnerDTO(2L, "Resource2", "Type2", 2L, "user2");

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.getAllResources()).thenReturn(Optional.of(List.of(resource1, resource2)));
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.getUserById(2L)).thenReturn(Optional.of(user2));
        when(resourceMapper.toResourceWithOwnerDTO(resource1, user1)).thenReturn(resourceDTO1);
        when(resourceMapper.toResourceWithOwnerDTO(resource2, user2)).thenReturn(resourceDTO2);
        when(req.getSession(false)).thenReturn(session);

        List<ResourceWithOwnerDTO> result = resourceService.getAllResources(req);

        assertEquals(List.of(resourceDTO1, resourceDTO2), result);
        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getAllResources();
        verify(userRepository).getUserById(1L);
        verify(userRepository).getUserById(2L);
        verify(resourceMapper).toResourceWithOwnerDTO(resource1, user1);
        verify(resourceMapper).toResourceWithOwnerDTO(resource2, user2);
    }

    @Test
    @DisplayName("Update Resource")
    void updateResource() {
        UpdateResourceRequestDTO requestDTO = new UpdateResourceRequestDTO("UpdatedResource");
        ResourceType resourceType = ResourceType.CONFERENCE_ROOM;
        UserDTO currentUser = new UserDTO(1L, "user", "USER");
        Resource resource = Resource.builder()
                .id(1L)
                .userId(1L)
                .name("Resource1")
                .type("Type1")
                .build();
        Resource updatedResource = Resource.builder()
                .id(1L)
                .userId(1L)
                .name("UpdatedResource")
                .type(resourceType.name())
                .build();
        ResourceDTO resourceDTO = new ResourceDTO(1L, 1L, "UpdatedResource", resourceType.name());

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        doNothing().when(authUtil).authorize(currentUser, resource.getUserId());
        when(resourceRepository.updateResource(resource)).thenReturn(updatedResource);
        when(resourceMapper.toDTO(updatedResource)).thenReturn(resourceDTO);
        when(req.getSession(false)).thenReturn(session);

        ResourceDTO result = resourceService.updateResource(1L, requestDTO, resourceType, req);

        assertEquals(resourceDTO, result);
        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getResourceById(1L);
        verify(authUtil).authorize(currentUser, resource.getUserId());
        verify(resourceRepository).updateResource(resource);
        verify(resourceMapper).toDTO(updatedResource);
    }

    @Test
    @DisplayName("Update Resource - Resource Not Found")
    void updateResource_ResourceNotFound() {
        UpdateResourceRequestDTO requestDTO = new UpdateResourceRequestDTO("UpdatedResource");
        ResourceType resourceType = ResourceType.CONFERENCE_ROOM;
        UserDTO currentUser = new UserDTO(1L, "user", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());
        when(req.getSession(false)).thenReturn(session);

        assertThrows(ResourceNotFoundException.class, () -> resourceService.updateResource(1L, requestDTO, resourceType, req));

        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getResourceById(1L);
        verify(authUtil, never()).authorize(any(UserDTO.class), anyLong());
        verify(resourceRepository, never()).updateResource(any(Resource.class));
        verify(resourceMapper, never()).toDTO(any(Resource.class));
    }

    @Test
    @DisplayName("Delete Resource")
    void deleteResource() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");
        Resource resource = Resource.builder()
                .id(1L)
                .userId(1L)
                .name("Resource1")
                .type("Type1")
                .build();

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(req.getSession(false)).thenReturn(session);
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.of(resource));
        doNothing().when(authUtil).authorize(currentUser, resource.getUserId());

        resourceService.deleteResource(1L, req);

        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getResourceById(1L);
        verify(authUtil).authorize(currentUser, resource.getUserId());
        verify(resourceRepository).deleteResource(1L);
    }

    @Test
    @DisplayName("Delete Resource - Resource Not Found")
    void deleteResource_ResourceNotFound() {
        UserDTO currentUser = new UserDTO(1L, "user", "USER");

        when(authUtil.authenticate(req, "USER")).thenReturn(currentUser);
        when(resourceRepository.getResourceById(1L)).thenReturn(Optional.empty());
        when(req.getSession(false)).thenReturn(session);

        assertThrows(ResourceNotFoundException.class, () -> resourceService.deleteResource(1L, req));

        verify(authUtil).authenticate(req, "USER");
        verify(resourceRepository).getResourceById(1L);
        verify(authUtil, never()).authorize(any(UserDTO.class), anyLong());
        verify(resourceRepository, never()).deleteResource(anyLong());
    }
}
