package ru.y_lab;

import ru.y_lab.config.LiquibaseInitializer;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.service.impl.BookingServiceImpl;
import ru.y_lab.service.impl.ResourceServiceImpl;
import ru.y_lab.service.impl.UserServiceImpl;
import ru.y_lab.ui.BookingUI;
import ru.y_lab.ui.ResourceUI;
import ru.y_lab.ui.impl.ActionExecutor;
import ru.y_lab.ui.impl.BookingConsoleUI;
import ru.y_lab.ui.impl.MenuManager;
import ru.y_lab.ui.impl.ResourceConsoleUI;
import ru.y_lab.util.ConsoleInputReader;
import ru.y_lab.util.InputReader;

/**
 * The CoworkingServiceApp class serves as the main application entry point for managing coworking resources and bookings.
 */
public class CoworkingServiceApp {
    private static final InputReader inputReader = new ConsoleInputReader();
    private static final UserRepository userRepository = new UserRepository();
    private static final UserService userService = new UserServiceImpl(inputReader, userRepository);
    private static final ResourceUI resourceUI = new ResourceConsoleUI();
    private static final ResourceRepository resourceRepository = new ResourceRepository();
    private static final ResourceService resourceService = new ResourceServiceImpl(inputReader, resourceRepository, userService, resourceUI);
    private static final BookingUI bookingUI = new BookingConsoleUI();
    private static final BookingRepository bookingRepository = new BookingRepository();
    private static final BookingService bookingService = new BookingServiceImpl(inputReader, bookingRepository, userService, resourceService, bookingUI);
    private static boolean running = true;

    /**
     * Main method to start the Coworking Service application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        MenuManager menuManager = new MenuManager(userService);
        ActionExecutor actionExecutor = new ActionExecutor(userService, resourceService, bookingService);

        LiquibaseInitializer.initialize();

        while (running) {
            menuManager.showMenu();
            int choice = inputReader.getUserChoice();
            try {
                running = actionExecutor.executeChoice(choice);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        inputReader.close();
        System.out.println("Exiting the application. Goodbye!");
    }

}
