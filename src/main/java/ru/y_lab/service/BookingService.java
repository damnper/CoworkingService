package ru.y_lab.service;

import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;

/**
 * The BookingService interface defines methods for managing bookings.
 */
public interface BookingService {

    /**
     * Method to manage bookings, handling conflicts and exceptions.
     * @throws UserNotFoundException if a required user is not found
     */
    void manageBookings() throws UserNotFoundException;

    /**
     * Method to filter bookings based on resource and user.
     */
    void filterBookings();

    /**
     * Adds a new booking based on user input.
     *
     * @throws BookingConflictException if there is a conflict with an existing booking
     */
    void addBooking() throws BookingConflictException;

    /**
     * Cancels an existing booking based on user input.
     * Prompts the user for confirmation before proceeding with cancellation.
     */
    void cancelBooking();

    /**
     * Displays all bookings made by the current user.
     *
     * @throws ResourceNotFoundException if a resource referenced by a booking is not found
     * @throws UserNotFoundException if the current user is not found
     */
    void viewUserBookings() throws UserNotFoundException, ResourceNotFoundException;

    /**
     * Updates an existing booking based on user input.
     * Allows the user to change the booking start and end times.
     *
     * @throws BookingConflictException if there is a conflict with an existing booking\
     */
    void updateBooking() throws BookingConflictException;

    /**
     * Displays available booking slots for a specified date and resource.
     * Prompts the user to enter a resource ID and date, then shows available slots.
     */
    void viewAvailableSlots();

}
