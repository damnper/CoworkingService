package ru.y_lab.service;

import ru.y_lab.dto.*;

import java.util.List;

/**
 * The BookingService interface defines methods for managing bookings.
 */
public interface BookingService {

    /**
     * Adds a new booking to the system.
     *
     * @param token      the authentication token of the user making the request
     * @param requestDTO the request containing booking details
     * @return the added booking as a BookingDTO
     */
    BookingDTO addBooking(String token,
                          AddBookingRequestDTO requestDTO);

    /**
     * Retrieves a booking by its ID.
     *
     * @param token     the authentication token of the user making the request
     * @param bookingId the ID of the booking
     * @return the booking with owner and resource details as a BookingWithOwnerResourceDTO
     */
    BookingWithOwnerResourceDTO getBookingById(String token,
                                               Long bookingId);

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param token the authentication token of the user making the request
     * @return a list of bookings with owner and resource details for the specified user
     */
    List<BookingWithOwnerResourceDTO> getUserBookings(String token);

    /**
     * Retrieves all bookings in the system. Only accessible by admin users.
     *
     * @param token the authentication token of the admin user making the request
     * @return a list of all bookings with owner and resource details
     */
    List<BookingWithOwnerResourceDTO> getAllBookings(String token);

    /**
     * Retrieves bookings by a specific date.
     *
     * @param token the authentication token of the user making the request
     * @param date  the date of the bookings in milliseconds since epoch
     * @return a list of bookings with owner and resource details for the specified date
     */
    List<BookingWithOwnerResourceDTO> getBookingsByDate(String token,
                                                        Long date);

    /**
     * Retrieves bookings made by a specific user ID. Only accessible by admin users.
     *
     * @param userId the ID of the user
     * @return a list of bookings with owner and resource details for the specified user
     */
    List<BookingWithOwnerResourceDTO> getBookingsByUserId(Long userId);

    /**
     * Retrieves bookings for a specific resource ID.
     *
     * @param token      the authentication token of the user making the request
     * @param resourceId the ID of the resource
     * @return a list of bookings with owner and resource details for the specified resource
     */
    List<BookingWithOwnerResourceDTO> getBookingsByResourceId(String token,
                                                              Long resourceId);

    /**
     * Retrieves available slots for a specific resource and date.
     *
     * @param token   the authentication token of the user making the request
     * @param request the request containing resource ID and date
     * @return a list of available slots for the specified resource and date
     */
    List<AvailableSlotDTO> getAvailableSlots(String token,
                                             AvailableSlotsRequestDTO request);

    /**
     * Updates an existing booking.
     *
     * @param token      the authentication token of the user making the request
     * @param bookingId  the ID of the booking to be updated
     * @param request the update request containing updated booking details
     * @return the updated booking as a BookingDTO
     */
    BookingDTO updateBooking(String token,
                             Long bookingId,
                             UpdateBookingRequestDTO request);

    /**
     * Deletes a booking by its ID.
     *
     * @param token     the authentication token of the user making the request
     * @param bookingId the ID of the booking to be deleted
     */
    void deleteBooking(String token,
                       Long bookingId);
}
