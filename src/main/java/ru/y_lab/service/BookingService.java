package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.y_lab.dto.*;

import java.util.List;

/**
 * The BookingService interface defines methods for managing bookings.
 */
public interface BookingService {

    /**
     * Adds a new booking to the system.
     *
     * @param requestDTO the request containing booking details
     * @param httpRequest the HTTP request to get the session
     * @return the added booking as a BookingDTO
     */
    BookingDTO addBooking(AddBookingRequestDTO requestDTO, HttpServletRequest httpRequest);

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId the ID of the booking
     * @param httpRequest the HTTP request to get the session
     * @return the booking with owner and resource details as a BookingWithOwnerResourceDTO
     */
    BookingWithOwnerResourceDTO getBookingById(Long bookingId, HttpServletRequest httpRequest);

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified user
     */
    List<BookingWithOwnerResourceDTO> getUserBookings(Long userId, HttpServletRequest httpRequest);

    /**
     * Retrieves all bookings in the system. Only accessible by admin users.
     *
     * @param httpRequest the HTTP request to get the session
     * @return a list of all bookings with owner and resource details
     */
    List<BookingWithOwnerResourceDTO> getAllBookings(HttpServletRequest httpRequest);

    /**
     * Retrieves bookings by a specific date.
     *
     * @param date the date of the bookings
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified date
     */
    List<BookingWithOwnerResourceDTO> getBookingsByDate(Long date, HttpServletRequest httpRequest);

    /**
     * Retrieves bookings made by a specific user ID. Only accessible by admin users.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified user
     */
    List<BookingWithOwnerResourceDTO> getBookingsByUserId(Long userId, HttpServletRequest httpRequest);

    /**
     * Retrieves bookings for a specific resource ID.
     *
     * @param resourceId the ID of the resource
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified resource
     */
    List<BookingWithOwnerResourceDTO> getBookingsByResourceId(Long resourceId, HttpServletRequest httpRequest);

    /**
     * Retrieves available slots for a specific resource and date.
     *
     * @param request the request containing resource ID and date
     * @param httpRequest the HTTP request to get the session
     * @return a list of available slots for the specified resource and date
     */
    List<AvailableSlotDTO> getAvailableSlots(AvailableSlotsRequestDTO request, HttpServletRequest httpRequest);

    /**
     * Updates an existing booking.
     *
     * @param bookingId the ID of the booking to be updated
     * @param request the update request containing updated booking details
     * @param httpRequest the HTTP request to get the session
     * @return the updated booking as a BookingDTO
     */
    BookingDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request, HttpServletRequest httpRequest);

    /**
     * Deletes a booking by its ID.
     *
     * @param bookingId the ID of the booking to be deleted
     * @param httpRequest the HTTP request to get the session
     */
    void deleteBooking(Long bookingId, HttpServletRequest httpRequest);
}
