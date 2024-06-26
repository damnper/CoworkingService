package ru.y_lab.ui;

import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * The ActionExecutor class handles executing the actions based on user choice.
 */
public class ActionExecutor {
    private final UserService userService;
    private final ResourceService resourceService;
    private final BookingService bookingService;
    private boolean running = true;

    @FunctionalInterface
    interface CheckedRunnable {
        void run() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException;
    }

    private final Map<Integer, CheckedRunnable> userActions = new HashMap<>();
    private final Map<Integer, CheckedRunnable> adminActions = new HashMap<>();

    public ActionExecutor(UserService userService, ResourceService resourceService, BookingService bookingService) {
        this.userService = userService;
        this.resourceService = resourceService;
        this.bookingService = bookingService;

        userActions.put(1, userService::registerUser);
        userActions.put(2, userService::loginUser);
        userActions.put(3, resourceService::manageResources);
        userActions.put(4, bookingService::manageBookings);
        userActions.put(0, this::exitApplication);

        adminActions.put(1, userService::registerUser);
        adminActions.put(2, userService::loginUser);
        adminActions.put(3, userService::viewAllUsers);
        adminActions.put(4, resourceService::manageResources);
        adminActions.put(5, bookingService::manageBookings);
        adminActions.put(6, bookingService::filterBookings);
        adminActions.put(0, this::exitApplication);
    }

    /**
     * Executes the appropriate action based on user choice.
     *
     * @param choice the user's choice
     * @return false if the application should stop running, true otherwise
     * @throws BookingConflictException if there is a conflict in booking
     * @throws ResourceNotFoundException if a resource is not found
     * @throws UserNotFoundException if a user is not found
     */
    public boolean executeChoice(int choice) throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        CheckedRunnable action;
        if (userService.getCurrentUser() != null && userService.getCurrentUser().getRole().equals("ADMIN")) {
            action = adminActions.getOrDefault(choice, this::invalidChoice);
        } else {
            action = userActions.getOrDefault(choice, this::invalidChoice);
        }
        action.run();
        return running;
    }

    /**
     * Sets running to false to exit the application.
     */
    private void exitApplication() {
        running = false;
    }

    /**
     * Displays an invalid choice message.
     */
    private void invalidChoice() {
        System.out.println("Invalid choice. Please try again.");
    }
}
