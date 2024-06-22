package ru.y_lab.service;

import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;

/**
 * The BookingService interface defines methods for managing bookings.
 */
public interface BookingService {

    void manageBookings() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException;

    void filterBookings() throws ResourceNotFoundException, UserNotFoundException;
}
