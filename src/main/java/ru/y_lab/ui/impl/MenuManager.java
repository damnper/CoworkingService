package ru.y_lab.ui.impl;

import lombok.RequiredArgsConstructor;
import ru.y_lab.service.UserService;

import static ru.y_lab.constants.ColorTextCodes.*;

/**
 * The MenuManager class handles displaying the menu and getting user input.
 */
@RequiredArgsConstructor
public class MenuManager {

    private final UserService userService;

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
        System.out.println(BLUE + "\n--- Coworking Service Main Menu ---" + RESET);
        System.out.println("Logged in as: " + GREEN + currentUserName + RESET + "\n");
        System.out.println("1. " + YELLOW + "Register" + RESET);
        System.out.println("2. " + YELLOW + "Login" + RESET);
        System.out.println("3. " + YELLOW + "Manage Resources" + RESET);
        System.out.println("4. " + YELLOW + "Manage Bookings" + RESET);
        System.out.println("0. " + RED + "Exit" + RESET);
        System.out.print(CYAN + "\nEnter your choice: " + RESET);
    }

    /**
     * Displays the admin menu options for administrators.
     */
    private void showAdminMenu() {
        String currentUserName = userService.getCurrentUser() != null ? userService.getCurrentUser().getUsername() : "Guest";
        System.out.println(BLUE + "\n--- Coworking Service Admin Menu ---" + RESET);
        System.out.println("Logged in as: " + GREEN + currentUserName + RESET + "\n");
        System.out.println("1. " + YELLOW + "Register" + RESET);
        System.out.println("2. " + YELLOW + "Login" + RESET);
        System.out.println("3. " + YELLOW + "View All Users" + RESET);
        System.out.println("4. " + YELLOW + "Manage Resources" + RESET);
        System.out.println("5. " + YELLOW + "Manage Bookings" + RESET);
        System.out.println("6. " + YELLOW + "Filter Bookings" + RESET);
        System.out.println("0. " + RED + "Exit" + RESET);
        System.out.print(CYAN + "\nEnter your choice: " + RESET);
    }
}
