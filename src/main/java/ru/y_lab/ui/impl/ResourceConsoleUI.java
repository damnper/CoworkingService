package ru.y_lab.ui.impl;

import ru.y_lab.service.UserService;
import ru.y_lab.ui.ResourceUI;

public class ResourceConsoleUI implements ResourceUI {

    @Override
    public void showResourceMenu(UserService userService) {
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
}
