package ru.y_lab.service;

import ru.y_lab.dto.BookingDTO;

import java.util.List;

/**
 * The BookingService interface defines methods for managing bookings.
 */
public interface BookingService {

    /**
     * Adds a new booking based on user input.
     */
    BookingDTO addBooking(String bookingJson);

    /**
     * Updates an existing booking based on user input.
     * Allows the user to change the booking start and end times.
     */
    void updateBooking(String bookingJson);

    BookingDTO getBookingById(Long bookingId);

    /**
     * Displays all bookings made by the current user.
     */
    List<BookingDTO> viewUserBookings();

    /**
     * Cancels an existing booking based on user input.
     * Prompts the user for confirmation before proceeding with cancellation.
     */
    void deleteBooking(Long bookingId);


    /**
     * Method to filter bookings based on resource and user.
     */
    List<BookingDTO> filterBookings(String filterJson);

    /**
     * Displays available booking slots for a specified date and resource.
     * Prompts the user to enter a resource ID and date, then shows available slots.
     */
    List<BookingDTO> viewAvailableSlots(Long resourceId, String dateStr);

}
