package ru.y_lab;

import ru.y_lab.repo.BookingRepository;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.service.impl.BookingServiceImpl;
import ru.y_lab.service.impl.ResourceServiceImpl;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.ui.ActionExecutor;
import ru.y_lab.util.ConsoleInputReader;
import ru.y_lab.util.InputReader;
import ru.y_lab.ui.MenuManager;

/**
 * The CoworkingServiceApp class serves as the main application entry point for managing coworking resources and bookings.
 */
public class CoworkingServiceApp {
    private static final InputReader inputReader = new ConsoleInputReader();
    private static final UserRepository userRepository = new UserRepository();
    private static final UserService userService = new UserServiceImpl(inputReader, userRepository, true);
    private static final ResourceRepository resourceRepository = new ResourceRepository();
    private static final ResourceService resourceService = new ResourceServiceImpl(inputReader, resourceRepository, userService);
    private static final BookingRepository bookingRepository = new BookingRepository();
    private static final BookingService bookingService = new BookingServiceImpl(inputReader, bookingRepository, userService, resourceService);
    private static boolean running = true;

    /**
     * Main method to start the Coworking Service application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        MenuManager menuManager = new MenuManager(userService);
        ActionExecutor actionExecutor = new ActionExecutor(userService, resourceService, bookingService);

        while (running) {
            menuManager.showMenu();
            int choice = MenuManager.getUserChoice();
            try {
                running = actionExecutor.executeChoice(choice);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Exiting the application. Goodbye!");
    }

}
