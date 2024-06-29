package ru.y_lab.service.impl;

import lombok.SneakyThrows;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceConflictException;
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
import java.util.Optional;

import static ru.y_lab.constants.ColorTextCodes.*;

/**
 * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
 * It interacts with the ResourceRepository to perform CRUD operations.
 */
public class ResourceServiceImpl implements ResourceService {

    private final InputReader inputReader;
    private final ResourceRepository resourceRepository;
    private final UserService userService;
    private final ResourceUI resourceUI;
    private boolean running;

    private final Map<Integer, CheckedRunnable> resourceActions = new HashMap<>();

    @FunctionalInterface
    interface CheckedRunnable {
        void run() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException, ResourceConflictException;
    }

    /**
     * Constructs a ResourceServiceImpl with the specified dependencies and initializes the resource actions.
     * @param inputReader the InputReader instance for reading user input
     * @param resourceRepository the ResourceRepository instance for accessing resource data
     * @param userService the UserService instance providing user-related operations
     * @param resourceUI the ResourceUI instance for displaying resource-related UI components
     */
    public ResourceServiceImpl(InputReader inputReader, ResourceRepository resourceRepository, UserService userService, ResourceUI resourceUI) {
        this.userService = userService;
        this.inputReader = inputReader;
        this.resourceRepository = resourceRepository;
        this.resourceUI = resourceUI;

        // Initialize the resource action mappings
        resourceActions.put(1, this::addResource);
        resourceActions.put(2, this::viewResources);
        resourceActions.put(3, this::updateResource);
        resourceActions.put(4, this::deleteResources);
        resourceActions.put(0, this::exitApplication);
    }

    /**
     * Manages resources by displaying the resource menu and executing the selected action.
     * Checks if the current user has access to manage resources.
     * Keeps running and showing the menu until the user decides to exit.
     */
    @Override
    public void manageResources() {
        if (checkUserAccess()) return;

        running = true;
        while (running) {
            resourceUI.showResourceMenu(userService);
            int choice = inputReader.getUserChoice();
            try {
                running = executeChoice(choice);
            } catch (Exception e) {
                System.err.println(RED + "Error: " + e.getMessage() + "\n" + RESET);
            }
        }
    }

    /**
     * Retrieves a resource by its unique identifier.
     * @param resourceId the ID of the resource to retrieve
     * @return the resource with the specified ID
     */
    @Override
    @SneakyThrows
    public Resource getResourceById(Long resourceId) {
        Optional<Resource> resourceOptional = resourceRepository.getResourceById(resourceId);
        return resourceOptional.orElseThrow(() -> new ResourceNotFoundException("Resource with ID " + resourceId + " not found"));

    }

    /**
     * Displays a list of all resources retrieved from the resource repository.
     * If no resources are found, it prints a message indicating that no resources are available.
     * Otherwise, it uses the resource UI to show the available resources.
     * This method uses @SneakyThrows to automatically handle any thrown exceptions.
     */
    @Override
    @SneakyThrows
    public void viewResources() {
        List<Resource> resources = resourceRepository.getAllResources();
        if (resources.isEmpty()) {
            System.out.println(YELLOW + "\nNo resources available." + RESET);
        } else {
            resourceUI.showAvailableResources(resources, userService);
        }
    }

    /**
     * Adds a new resource based on user input.
     * Prompts the user to enter the resource name and type.
     * Builds a Resource object and attempts to add it to the repository.
     * If a conflict occurs, throws a ResourceConflictException.
     */
    @SneakyThrows
    private void addResource() {
        System.out.print(CYAN + "Enter resource name: " + RESET);
        String name = inputReader.readLine();
        System.out.print(CYAN + "Enter resource type (Workspace/Conference Room): " + RESET);
        String type = inputReader.readLine();

        Resource resource = Resource.builder()
                .userId(userService.getCurrentUser().getId())
                .name(name)
                .type(type)
                .build();

        try {
            resourceRepository.addResource(resource);
            System.out.println(GREEN + "Resource added successfully!" + RESET);
        } catch (Exception e) {
            throw new ResourceConflictException("Resource conflict: The resource is already exist.");
        }
    }

    /**
     * Updates an existing resource based on user input.
     * Prompts the user to enter the resource ID, new name, and new type.
     */
    @SneakyThrows
    public void updateResource() {
        System.out.print(CYAN + "Enter resource ID: " + RESET);
        Long resourceId = Long.parseLong(inputReader.readLine());

        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(YELLOW + "Resource with ID " + resourceId + " not found" + RESET));

        if (checkResourceAccess(resource, "Permission denied: You can only update your own resources.")) return;

        System.out.print(CYAN + "Enter new resource name: " + RESET);
        String newName = inputReader.readLine();
        System.out.print(CYAN + "Enter new resource type: " + RESET);
        String newType = inputReader.readLine();

        resource.setName(newName);
        resource.setType(newType);

        resourceRepository.updateResource(resource);
        System.out.println(GREEN + "Resource updated successfully!" + RESET);
    }

    /**
     * Deletes a resource based on user input.
     * Prompts the user to enter the resource ID.
     * Attempts to delete the resource if the current user has access rights.
     */ 
    public void deleteResources() {
        System.out.print(CYAN + "Enter resource ID: " + RESET);
        Long resourceId = Long.parseLong(inputReader.readLine());

        try {
            Resource resource = resourceRepository.getResourceById(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource with ID " + resourceId + " not found"));

            if (checkResourceAccess(resource, RED + "Permission denied: You can only delete your own resource." + RESET))
                return;

            resourceRepository.deleteResource(resourceId);
            System.out.println(GREEN + "\nResource deleted successfully!" + RESET);
        } catch (ResourceNotFoundException e) {
            System.out.println(RED + "Resource not found: " + e.getMessage() + RESET);
        }
    }

    /**
     * Checks if the current user has access to the specified resource.
     * Prints an error message if the user does not have the necessary permissions.
     * @param resource the resource to check access for
     * @param message the error message to display if access is denied
     * @return true if access is denied, false otherwise
     */
    private boolean checkResourceAccess(Resource resource, String message) {
        if (!userService.getCurrentUser().getRole().equals("ADMIN") && !resource.getUserId().equals(userService.getCurrentUser().getId())) {
            System.out.println(message);
            return true;
        }
        return false;
    }

    /**
     * Checks if the current user is logged in.
     * Prints an error message and returns true if the user is not logged in.
     * @return true if access is denied, false otherwise
     */
    private boolean checkUserAccess() {
        if (userService.getCurrentUser() == null) {
            System.out.println(RED + "\nAccess denied. Please log in to manage resources." + RESET);
            return true;
        }
        return false;
    }

    /**
     * Executes the chosen action based on the user's input.
     * @param choice the integer corresponding to the user's chosen action
     * @return true if the application should continue running, false if it should exit
     */
    @SneakyThrows
    private boolean executeChoice(int choice) {
        CheckedRunnable action = getActionsMap().getOrDefault(choice, this::invalidChoice);
        action.run();
        return running;
    }

    /**
     * Determines the appropriate actions map based on the user's role.
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
        System.out.println(RED + "Invalid choice. Please try again." + RESET);
    }
}
