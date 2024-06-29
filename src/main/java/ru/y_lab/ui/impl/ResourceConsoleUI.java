package ru.y_lab.ui.impl;

import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Resource;
import ru.y_lab.service.UserService;
import ru.y_lab.ui.ResourceUI;

import java.util.List;

import static ru.y_lab.constants.ColorTextCodes.*;

/**
 * Console-based implementation of the ResourceUI interface providing methods
 * to display resource management functionalities and available resources.
 */
public class ResourceConsoleUI implements ResourceUI {

    /**
     * Displays the menu for managing resources based on the current user's permissions.
     * Shows options for adding, viewing, updating, and deleting resources,
     * as well as returning to the main menu.
     * Requires the user to be logged in to display personalized information.
     * @param userService the UserService instance providing user-related operations
     */
    @Override
    public void showResourceMenu(UserService userService) {
        String currentUserName = userService.getCurrentUser().getUsername();
        System.out.println(BLUE + "\n--- Manage Resources ---" + RESET);
        System.out.println("Logged in as: " + GREEN + currentUserName + RESET + "\n");
        System.out.println("1. " + YELLOW + "Add Resource" + RESET);
        System.out.println("2. " + YELLOW + "View Resources" + RESET);
        System.out.println("3. " + YELLOW + "Update Resource" + RESET);
        System.out.println("4. " + YELLOW + "Delete Resources" + RESET);
        System.out.println("0. " + RED + "Back to Main Menu" + RESET);
        System.out.print(CYAN + "\nEnter your choice: " + RESET);
    }

    /**
     * Displays details of each available resource in the provided list.
     * @param resources the list of resources to display details for
     * @param userService the UserService instance providing user-related operations
     * @throws UserNotFoundException if the owner user for a resource cannot be found
     */
    @Override
    public void showAvailableResources(List<Resource> resources, UserService userService) throws UserNotFoundException {
        System.out.println(BLUE + "\n--- Available Resources ---\n" + RESET);
        System.out.println(CYAN + "----------------------------" + RESET);
        for (Resource resource : resources) {
            String username = userService.getUserById(resource.getUserId()).getUsername();
            System.out.println(PURPLE + "Resource ID   : " + resource.getId() + RESET);
            System.out.println(PURPLE + "Organizer     : " + username + RESET);
            System.out.println(PURPLE + "Resource Name : " + resource.getName() + RESET);
            System.out.println(PURPLE + "Resource Type : " + resource.getType() + RESET);
            System.out.println(CYAN + "----------------------------" + RESET);
        }
    }
}
