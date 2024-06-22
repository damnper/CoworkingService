package ru.y_lab.service.impl;

import ru.y_lab.CoworkingServiceApp;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * The ResourceServiceImpl class provides an implementation of the ResourceService interface.
 * It interacts with the ResourceRepository to perform CRUD operations.
 */
public class ResourceServiceImpl implements ResourceService {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ResourceRepository resourceRepository = new ResourceRepository();
    private final UserService userService;

    public ResourceServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Resource getResourceById(String ig) throws ResourceNotFoundException {
        Resource resource = resourceRepository.getResourceById(ig);
        if (resource == null) {
            throw new ResourceNotFoundException("\nResource not found.");
        }
        return resource;
    }

    @Override
    public void manageResources() throws ResourceNotFoundException, UserNotFoundException {
        if (userService.getCurrentUser() == null) {
            System.out.println("\nAccess denied. Please log in to manage resources.");
            return;
        }

        boolean managingResources = true;
        while (managingResources) {
            showResourceMenu();
            int choice = CoworkingServiceApp.getUserChoice();
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


    private void addResource() {
        System.out.print("Enter resource name: ");
        String name = scanner.nextLine();
        System.out.print("Enter resource type (Workspace/Conference Room): ");
        String type = scanner.nextLine();

        Resource resource = new Resource(UUID.randomUUID().toString(), userService.getCurrentUser().getId(), name, type);
        try {
            resourceRepository.addResource(resource);
            System.out.println("Resource added successfully!");
        } catch (ResourceNotFoundException e) {
            System.out.println("Resource conflict: " + e.getMessage());
        }
    }

    private void updateResource() throws ResourceNotFoundException {
        System.out.print("Enter resource ID: ");
        String bookingId = scanner.nextLine();

        Resource resource = resourceRepository.getResourceById(bookingId);
        if (resource == null) {
            System.out.println("Resource not found.");
            return;
        }

        if (!userService.getCurrentUser().getRole().equals("ADMIN") && !resource.getUserId().equals(userService.getCurrentUser().getId())) {
            System.out.println("Permission denied: You can only update your own resources.");
            return;
        }

        System.out.print("Enter new resource name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new resource type: ");
        String newDescription = scanner.nextLine();

        resource.setName(newName);
        resource.setType(newDescription);

        resourceRepository.updateResource(resource);
        System.out.println("Resource updated successfully!");
    }

    private void deleteResources() {
        System.out.print("Enter resource ID: ");
        String resourceId = scanner.nextLine();

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
