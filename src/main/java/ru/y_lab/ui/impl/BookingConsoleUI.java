package ru.y_lab.ui.impl;

import ru.y_lab.service.UserService;
import ru.y_lab.ui.BookingUI;
import ru.y_lab.util.InputReader;

public class BookingConsoleUI implements BookingUI {

    /**
     * Displays the menu for managing bookings based on the current user's permissions.
     * Shows options for adding, canceling, viewing, and updating bookings,
     * as well as viewing available slots and returning to the main menu.
     * Requires the user to be logged in to display personalized information.
     */
    @Override
    public void showBookingMenu(UserService userService) {
        String currentUserName = userService.getCurrentUser().getUsername();
        System.out.println("\n--- Manage Bookings ---\n");
        System.out.println("Logged in as: " + currentUserName + "\n");
        System.out.println("1. Add Booking");
        System.out.println("2. Cancel my Booking");
        System.out.println("3. View my Bookings");
        System.out.println("4. Change my Booking");
        System.out.println("5. View Available Slots");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }


    @Override
    public void showFilterMenu(UserService userService, InputReader inputReader) {
        String currentUserName = userService.getCurrentUser().getUsername();
        System.out.println("\n--- Filter Bookings ---\n");
        System.out.println("Logged in as: " + currentUserName + "\n");
        System.out.println("1. By Date");
        System.out.println("2. By User");
        System.out.println("3. By Resource");
        System.out.print("Enter your choice: ");
    }

}
