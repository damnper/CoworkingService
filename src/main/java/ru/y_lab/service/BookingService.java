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
     * @throws BookingConflictException if there is a conflict with an existing booking
     * @throws ResourceNotFoundException if a required resource is not found
     * @throws UserNotFoundException if a required user is not found
     */
    void manageBookings() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException;

    /**
     * Method to filter bookings based on resource and user.
     * @throws ResourceNotFoundException if a required resource is not found
     * @throws UserNotFoundException if a required user is not found
     */
    void filterBookings() throws ResourceNotFoundException, UserNotFoundException;
}
