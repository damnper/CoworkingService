package ru.y_lab;

import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.service.impl.BookingServiceImpl;
import ru.y_lab.service.impl.ResourceServiceImpl;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.util.ConsoleInputReader;
import ru.y_lab.util.InputReader;

/**
 * The CoworkingServiceApp class serves as the main application entry point for managing coworking resources and bookings.
 * It provides menus for users and administrators to interact with the system.
 */
public class CoworkingServiceApp {
    private static final InputReader inputReader = new ConsoleInputReader();
    private static final UserRepository userRepository = new UserRepository();
    private static final UserService userService = new UserServiceImpl(inputReader, userRepository, true);
    private static final ResourceRepository resourceRepository = new ResourceRepository();
    private static final ResourceService resourceService = new ResourceServiceImpl(inputReader, resourceRepository, userService);
    private static final BookingRepository bookingRepository = new BookingRepository();
    private static final BookingService bookingService = new BookingServiceImpl(inputReader, bookingRepository, userService, resourceService);

    /**
     * Main method to start the Coworking Service application.
     *
     * @param args command-line arguments (not used)
     * @throws BookingConflictException if there is a conflict in booking
     * @throws ResourceNotFoundException if a resource is not found
     * @throws UserNotFoundException if a user is not found
     */
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

    /**
     * Displays the main menu options for regular users.
     */
    public static void showMainMenu() {
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
    public static void showAdminMenu() {
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

    /**
     * Reads the user's choice from the console input.
     *
     * @return the integer representing the user's choice
     */
    public static int getUserChoice() {
        try {
            return Integer.parseInt(inputReader.readLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
