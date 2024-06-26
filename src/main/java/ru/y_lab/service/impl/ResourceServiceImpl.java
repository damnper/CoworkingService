package ru.y_lab.service.impl;

import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.util.InputReader;
import ru.y_lab.ui.MenuManager;

import java.util.List;
import java.util.UUID;

/**
 * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
 * It interacts with the ResourceRepository to perform CRUD operations.
 */
public class ResourceServiceImpl implements ResourceService {

    private final InputReader inputReader;
    private final ResourceRepository resourceRepository;
    private final UserService userService;

    /**
     * Constructs a ResourceServiceImpl object with the specified dependencies.
     * @param inputReader the input reader used for reading user input
     * @param resourceRepository the repository managing resources
     * @param userService the service providing user-related operations
     */
    public ResourceServiceImpl(InputReader inputReader, ResourceRepository resourceRepository, UserService userService) {
        this.userService = userService;
        this.inputReader = inputReader;
        this.resourceRepository = resourceRepository;
    }

    /**
     * Retrieves a resource by its unique identifier.
     * @param resourceId the ID of the resource to retrieve
     * @return the resource with the specified ID
     * @throws ResourceNotFoundException if the resource with the given ID is not found
     */
    @Override
    public Resource getResourceById(String resourceId) throws ResourceNotFoundException {
        Resource resource = resourceRepository.getResourceById(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("\nResource not found.");
        }
        return resource;
    }

    /**
     * Manages resources including CRUD operations.
     * @throws ResourceNotFoundException if a resource is not found
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public void manageResources() throws ResourceNotFoundException, UserNotFoundException {
        if (userService.getCurrentUser() == null) {
            System.out.println("\nAccess denied. Please log in to manage resources.");
            return;
        }

        boolean managingResources = true;
        while (managingResources) {
            showResourceMenu();
            int choice = MenuManager.getUserChoice();
            switch (choice) {
                case 1:
                    addResource();
                    break;
                case 2:
                    viewResources();
                    break;
                case 3:
                    updateResource();
                    break;
                case 4:
                    deleteResources();
                    break;
                case 0:
                    managingResources = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    /**
     * Displays a list of available resources.
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public void viewResources() throws UserNotFoundException {
        List<Resource> resources = resourceRepository.getAllResources();
        if (resources.isEmpty()) {
            System.out.println("\nNo resources available.");
        } else {
            System.out.println("\n--- Available Resources ---\n");
            for (Resource resource : resources) {
                String username = userService.getUserById(resource.getUserId()).getUsername();
                System.out.println("Resource ID   : " + resource.getId());
                System.out.println("Organizer     : " + username);
                System.out.println("Resource Name : " + resource.getName());
                System.out.println("Resource Type : " + resource.getType());
                System.out.println("\n----------------------------");
            }
        }
    }

    /**
     * Displays the menu for managing resources.
     */
    private void showResourceMenu() {
        String currentUserName = userService.getCurrentUser().getUsername();
        System.out.println("\n--- Manage Resources ---\n");
        System.out.println("Logged in as: " + currentUserName + "\n");
        System.out.println("1. Add Resource");
        System.out.println("2. View Resources");
        System.out.println("3. Update Resource");
        System.out.println("4. Delete Resources");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    /**
     * Adds a new resource based on user input.
     */
    public void addResource() {
        System.out.print("Enter resource name: ");
        String name = inputReader.readLine();
        System.out.print("Enter resource type (Workspace/Conference Room): ");
        String type = inputReader.readLine();

        Resource resource = new Resource(UUID.randomUUID().toString(), userService.getCurrentUser().getId(), name, type);
        try {
            resourceRepository.addResource(resource);
            System.out.println("Resource added successfully!");
        } catch (ResourceNotFoundException e) {
            System.out.println("Resource conflict: " + e.getMessage());
        }
    }

    /**
     * Updates an existing resource based on user input.
     * @throws ResourceNotFoundException if the resource to update is not found
     */
    public void updateResource() throws ResourceNotFoundException {
        System.out.print("Enter resource ID: ");
        String resourceId = inputReader.readLine();

        Resource resource = resourceRepository.getResourceById(resourceId);
        if (resource == null) {
            System.out.println("Resource not found.");
            return;
        }

        if (!userService.getCurrentUser().getRole().equals("ADMIN") && !resource.getUserId().equals(userService.getCurrentUser().getId())) {
            System.out.println("Permission denied: You can only update your own resources.");
            return;
        }

        System.out.print("Enter new resource name: ");
        String newName = inputReader.readLine();
        System.out.print("Enter new resource type: ");
        String newType = inputReader.readLine();

        resource.setName(newName);
        resource.setType(newType);

        resourceRepository.updateResource(resource);
        System.out.println("Resource updated successfully!");
    }

    /**
     * Deletes a resource based on user input.
     */
    public void deleteResources() {
        System.out.print("Enter resource ID: ");
        String resourceId = inputReader.readLine();

        try {
            Resource resource = resourceRepository.getResourceById(resourceId);
            if (resource == null) {
                System.out.println("Resource not found.");
                return;
            }

            if (!userService.getCurrentUser().getRole().equals("ADMIN") && !resource.getUserId().equals(userService.getCurrentUser().getId())) {
                System.out.println("Permission denied: You can only delete your own resource.");
                return;
            }

            resourceRepository.deleteResource(resourceId);
            System.out.println("\nResource deleted successfully!");
        } catch (ResourceNotFoundException e) {
            System.out.println("Resource not found: " + e.getMessage());
        }
    }
}
