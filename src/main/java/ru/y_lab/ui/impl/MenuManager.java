package ru.y_lab.ui.impl;

import ru.y_lab.service.UserService;

/**
 * The MenuManager class handles displaying the menu and getting user input.
 */
public class MenuManager {
    private final UserService userService;

    public MenuManager(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the appropriate menu based on the user's role.
     */
    public void showMenu() {
        if (userService.getCurrentUser() != null && userService.getCurrentUser().getRole().equals("ADMIN")) {
            showAdminMenu();
        } else {
            showMainMenu();
        }
    }

    /**
     * Displays the main menu options for regular users.
     */
    private void showMainMenu() {
        String currentUserName = userService.getCurrentUser() != null ? userService.getCurrentUser().getUsername() : "Guest";
        System.out.println("\n--- Coworking Service Main Menu ---");
        System.out.println("Logged in as: " + currentUserName + "\n");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Manage Resources");
        System.out.println("4. Manage Bookings");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
    }

    /**
     * Displays the admin menu options for administrators.
     */
    private void showAdminMenu() {
        String currentUserName = userService.getCurrentUser() != null ? userService.getCurrentUser().getUsername() : "Guest";
        System.out.println("\n--- Coworking Service Admin Menu ---");
        System.out.println("Logged in as: " + currentUserName + "\n");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. View All Users");
        System.out.println("4. Manage Resources");
        System.out.println("5. Manage Bookings");
        System.out.println("6. Filter Bookings");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
}
