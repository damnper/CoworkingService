package ru.y_lab.service.impl;

import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.ui.ResourceUI;
import ru.y_lab.util.InputReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
 * It interacts with the ResourceRepository to perform CRUD operations.
 */
public class ResourceServiceImpl implements ResourceService {

    private final InputReader inputReader;
    private final ResourceRepository resourceRepository;
    private final UserService userService;
    private final ResourceUI resourceUI;
    private boolean running = true;

    private final Map<Integer, CheckedRunnable> resourceActions = new HashMap<>();

    @FunctionalInterface
    interface CheckedRunnable {
        void run() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException;
    }

    /**
     * Constructs a ResourceServiceImpl object with the specified dependencies.
     * @param inputReader the input reader used for reading user input
     * @param resourceRepository the repository managing resources
     * @param userService the service providing user-related operations
     */
    public ResourceServiceImpl(InputReader inputReader, ResourceRepository resourceRepository, UserService userService, ResourceUI resourceUI) {
        this.userService = userService;
        this.inputReader = inputReader;
        this.resourceRepository = resourceRepository;
        this.resourceUI = resourceUI;

        resourceActions.put(1, this::addResource);
        resourceActions.put(2, this::viewResources);
        resourceActions.put(3, this::updateResource);
        resourceActions.put(4, this::deleteResources);
        resourceActions.put(0, this::exitApplication);
    }

    /**
     * Manages resources including CRUD operations.
     */
    @Override
    public void manageResources() {
        if (userService.getCurrentUser() == null) {
            System.out.println("\nAccess denied. Please log in to manage resources.");
            return;
        }

        while (running) {
            resourceUI.showResourceMenu(userService);
            int choice = inputReader.getUserChoice();
            try {
                running = executeChoice(choice);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
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

    /**
     * Executes the chosen action based on the user's input.
     *
     * @param choice the integer corresponding to the user's chosen action
     * @return true if the application should continue running, false if it should exit
     */
    private boolean executeChoice(int choice) throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        CheckedRunnable action = getActionsMap().getOrDefault(choice, this::invalidChoice);
        action.run();
        return running;
    }

    /**
     * Determines the appropriate actions map based on the user's role.
     *
     * @return the map of actions for the current user
     */
    private Map<Integer, CheckedRunnable> getActionsMap() {
        return resourceActions;
    }

    /**
     * Sets running to false to exit the application.
     */
    private void exitApplication() {
        running = false;
    }

    /**
     * Displays an invalid choice message.
     */
    private void invalidChoice() {
        System.out.println("Invalid choice. Please try again.");
    }
}
