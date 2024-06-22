package ru.y_lab;

import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.service.impl.BookingServiceImpl;
import ru.y_lab.service.impl.ResourceServiceImpl;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.util.ConsoleInputReader;
import ru.y_lab.util.InputReader;

import java.util.Scanner;

public class CoworkingServiceApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InputReader inputReader = new ConsoleInputReader();
    private static final UserService userService = new UserServiceImpl(inputReader);
    private static final ResourceService resourceService = new ResourceServiceImpl(userService);
    private static final BookingService bookingService = new BookingServiceImpl(userService, resourceService);

    public static void main(String[] args) throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        boolean running = true;
        while (running) {
            if (userService.getCurrentUser() != null && userService.getCurrentUser().getRole().equals("ADMIN")) {
                showAdminMenu();
            } else {
                showMainMenu();
            }
            int choice = getUserChoice();
            switch (choice) {
                case 1:
                    userService.registerUser();
                    break;
                case 2:
                    userService.loginUser();
                    break;
                case 3:
                    if (userService.getCurrentUser() != null && userService.getCurrentUser().getRole().equals("ADMIN")) {
                        userService.viewAllUsers();
                    } else {
                        resourceService.manageResources();
                    }
                    break;
                case 4:
                    if (userService.getCurrentUser() != null && userService.getCurrentUser().getRole().equals("ADMIN")) {
                        resourceService.manageResources();
                    } else {
                        bookingService.manageBookings();
                    }
                    break;
                case 5:
                    if (userService.getCurrentUser() != null && userService.getCurrentUser().getRole().equals("ADMIN"))
                        bookingService.manageBookings();
                    break;
                case 6:
                    if (userService.getCurrentUser() != null && userService.getCurrentUser().getRole().equals("ADMIN"))
                        bookingService.filterBookings();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Exiting the application. Goodbye!");
    }

    private static void showMainMenu() {
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

    private static void showAdminMenu() {
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

    public static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
