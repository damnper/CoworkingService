package ru.y_lab.ui.impl;

import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Booking;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.ui.BookingUI;
import ru.y_lab.util.InputReader;

import java.util.List;

import static ru.y_lab.constants.ColorTextCodes.*;

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
        System.out.println(BLUE + "\n--- Manage Bookings ---" + RESET + "\n");
        System.out.println("Logged in as: " + GREEN + currentUserName + RESET + "\n");
        System.out.println("1. " + YELLOW + "Add Booking" + RESET);
        System.out.println("2. " + YELLOW + "Cancel my Booking" + RESET);
        System.out.println("3. " + YELLOW + "View my Bookings" + RESET);
        System.out.println("4. " + YELLOW + "Change my Booking" + RESET);
        System.out.println("5. " + YELLOW + "View Available Slots" + RESET);
        System.out.println("0. " + RED + "Back to Main Menu" + RESET);
        System.out.print(CYAN + "\nEnter your choice: " + RESET);
    }

    /**
     * Displays a filter menu for selecting how to filter bookings.
     * Options include filtering by date, user, or resource.
     * Requires the user to be logged in to display personalized information.
     * @param userService the UserService instance providing user-related operations
     * @param inputReader the InputReader instance for reading user input
     */
    @Override
    public void showFilterMenu(UserService userService, InputReader inputReader) {
        String currentUserName = userService.getCurrentUser().getUsername();
        System.out.println(BLUE + "\n--- Filter Bookings ---" + RESET);
        System.out.println("Logged in as: " + GREEN + currentUserName + RESET + "\n");
        System.out.println("1. " + YELLOW + "By Date" + RESET);
        System.out.println("2. " + YELLOW + "By User" + RESET);
        System.out.println("3. " + YELLOW + "By Resource" + RESET);
        System.out.print(CYAN + "\nEnter your choice: " + RESET);
    }

    /**
     * Prints details of each booking in the provided list.
     * @param bookings the list of bookings to print details for
     * @throws UserNotFoundException if the user for a booking cannot be found
     * @throws ResourceNotFoundException if the resource for a booking cannot be found
     */
    @Override
    public void printBookings(List<Booking> bookings, UserService userService, ResourceService resourceService) throws UserNotFoundException, ResourceNotFoundException {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking booking : bookings) {
                String username = userService.getUserById(booking.getUserId()).getUsername();
                String resourceName = resourceService.getResourceById(booking.getResourceId()).getName();
                String resourceType = resourceService.getResourceById(booking.getResourceId()).getType();
                String date = booking.getStartTime().toLocalDate().toString();
                String startTime = booking.getStartTime().toLocalTime().toString();
                String endTime = booking.getEndTime().toLocalTime().toString();

                String table = String.format(PURPLE +
                                "+----------------+--------------------+\n" +
                                "| Booking ID     | %-18d |\n" +
                                "| Resource ID    | %-18d |\n" +
                                "| User ID        | %-18d |\n" +
                                "| User           | %-18s |\n" +
                                "| Resource Name  | %-18s |\n" +
                                "| Resource Type  | %-18s |\n" +
                                "| Date           | %-18s |\n" +
                                "| Start Time     | %-18s |\n" +
                                "| End Time       | %-18s |\n" +
                                "+----------------+--------------------+\n" + RESET,
                        booking.getId(),
                        booking.getResourceId(),
                        booking.getUserId(),
                        username,
                        resourceName,
                        resourceType,
                        date,
                        startTime,
                        endTime
                );

                System.out.println(table);
            }
        }
    }

    /**
     * Prints details of each booking owned by the current user in the provided list.
     * @param userBookings the list of bookings to print details for
     * @throws UserNotFoundException if the current user for a booking cannot be found
     * @throws ResourceNotFoundException if the resource for a booking cannot be found
     */
    @Override
    public void showUserBooking(List<Booking> userBookings, UserService userService, ResourceService resourceService) throws UserNotFoundException, ResourceNotFoundException {
        System.out.println(BLUE + "\n--- My Bookings ---\n" + RESET);
        System.out.println(CYAN + "----------------------------" + RESET);
        for (Booking booking : userBookings) {
            String username = userService.getUserById(booking.getUserId()).getUsername();
            String resourceName = resourceService.getResourceById(booking.getResourceId()).getName();
            String date = booking.getStartTime().toLocalDate().toString();
            String startTime = booking.getStartTime().toLocalTime().toString();
            String endTime = booking.getEndTime().toLocalTime().toString();

            String table = String.format(
                    PURPLE +
                            "+----------------+--------------------+\n" +
                            "| Booking ID     | %-18d |\n" +
                            "| Booking owner  | %-18s |\n" +
                            "| Resource Name  | %-18s |\n" +
                            "| Date           | %-18s |\n" +
                            "| Time           | %-18s |\n" +
                            "+----------------+--------------------+\n" +
                            RESET +
                            CYAN +
                            "----------------------------" +
                            RESET,
                    booking.getId(),
                    username,
                    resourceName,
                    date,
                    startTime + " - " + endTime
            );

            System.out.println(table);
        }
    }
}
