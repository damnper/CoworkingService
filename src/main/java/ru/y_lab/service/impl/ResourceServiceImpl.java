//package ru.y_lab.service.impl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import ru.y_lab.dto.ResourceDTO;
//import ru.y_lab.exception.ResourceConflictException;
//import ru.y_lab.exception.ResourceNotFoundException;
//import ru.y_lab.mapper.CustomResourceMapper;
//import ru.y_lab.model.Resource;
//import ru.y_lab.repo.ResourceRepository;
//import ru.y_lab.service.ResourceService;
//import ru.y_lab.service.UserService;
//
//import java.util.List;
//
///**
// * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
// * It interacts with the ResourceRepository to perform CRUD operations.
// */
//@RequiredArgsConstructor
//public class ResourceServiceImpl implements ResourceService {
//
//    private final ResourceRepository resourceRepository = new ResourceRepository();
//    private final UserService userService = new UserServiceImpl();
//    private final ObjectMapper objectMapper = new ObjectMapper();
////    private final ResourceMapper resourceMapper = ResourceMapper.INSTANCE;
//    private final CustomResourceMapper resourceMapper = new CustomResourceMapper();
//
//
//    /**
//     * Adds a new resource based on user input.
//     * Prompts the user to enter the resource name and type.
//     * Builds a Resource object and attempts to add it to the repository.
//     * If a conflict occurs, throws a ResourceConflictException.
//     */
//    @Override
//    @SneakyThrows
//    public ResourceDTO addResource(String resourceJson) {
//        ResourceDTO resourceDTO = objectMapper.readValue(resourceJson, ResourceDTO.class);
//
//        Resource resource = resourceMapper.toEntity(resourceDTO);
//        resource.setUserId(userService.getCurrentUser().getId());
//
//        try {
//            resourceRepository.addResource(resource);
//            return resourceMapper.toDTO(resource);
//        } catch (Exception e) {
//            throw new ResourceConflictException("Resource conflict: The resource already exists.");
//        }
//    }
//
//
//    /**
//     * Updates an existing resource based on user input.
//     * Prompts the user to enter the resource ID, new name, and new type.
//     */
//    @Override
//    @SneakyThrows
//    public void updateResource(String resourceJson) {
//        ResourceDTO resourceDTO = objectMapper.readValue(resourceJson, ResourceDTO.class);
//        Long resourceId = resourceDTO.getId();
//
//        Resource existingResource = resourceRepository.getResourceById(resourceId)
//                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID " + resourceId + " not found"));
//
//        if (!userService.getCurrentUser().getRole().equals("ADMIN") && !existingResource.getUserId().equals(userService.getCurrentUser().getId())) {
//            throw new SecurityException("Permission denied: You can only update your own resources.");
//        }
//
//        existingResource.setName(resourceDTO.getName());
//        existingResource.setType(resourceDTO.getType());
//
//        resourceRepository.updateResource(existingResource);
//    }
//
//    /**
//     * Retrieves a resource by its unique identifier.
//     *
//     * @param resourceId the ID of the resource to retrieve
//     * @return the resource with the specified ID
//     */
//    @Override
//    @SneakyThrows
//    public ResourceDTO getResourceById(Long resourceId) {
//        Resource resource = resourceRepository.getResourceById(resourceId)
//                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID " + resourceId + " not found"));
//
//        return resourceMapper.toDTO(resource);
//    }
//
//    /**
//     * Displays a list of all resources retrieved from the resource repository.
//     * If no resources are found, it prints a message indicating that no resources are available.
//     * Otherwise, it uses the resource UI to show the available resources.
//     * This method uses @SneakyThrows to automatically handle any thrown exceptions.
//     */
//    @Override
//    @SneakyThrows
//    public List<ResourceDTO> viewResources() {
//        List<Resource> resources = resourceRepository.getAllResources();
//        return resources.stream().map(resourceMapper::toDTO).toList();
//    }
//
//    /**
//     * Deletes a resource based on user input.
//     * Prompts the user to enter the resource ID.
//     * Attempts to delete the resource if the current user has access rights.
//     */
//    @Override
//    @SneakyThrows
//    public void deleteResource(Long resourceId) {
//        Resource resource = resourceRepository.getResourceById(resourceId)
//                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID " + resourceId + " not found"));
//
//        if (!userService.getCurrentUser().getRole().equals("ADMIN") && !resource.getUserId().equals(userService.getCurrentUser().getId())) {
//            throw new SecurityException("Permission denied: You can only delete your own resource.");
//        }
//
//        resourceRepository.deleteResource(resourceId);
//    }
//
//}
