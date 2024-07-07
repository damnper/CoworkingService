package ru.y_lab.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The BookingService interface defines methods for managing bookings.
 */
public interface BookingService {

    /**
     * Adds a new booking based on user input.
     * Prompts the user to enter the resource ID and booking times.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void addBooking(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves a booking by its unique identifier.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void getBookingById(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Displays all bookings made by the current user.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void getUserBookings(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves all bookings in the system. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void getAllBookings(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Displays available booking slots for a specified date and resource.
     * Prompts the user to enter a resource ID and date, then shows available slots.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void getAvailableSlots(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves bookings by a specific date. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void getBookingsByDate(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves bookings by a specific user ID. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void getBookingsByUserId(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Retrieves bookings by a specific resource ID. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void getBookingsByResourceId(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Updates an existing booking based on user input.
     * Allows the user to change the booking start and end times.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    void updateBooking(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    /**
     * Cancels an existing booking based on user input.
     * Prompts the user for confirmation before proceeding with cancellation.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     */
    void deleteBooking(HttpServletRequest req, HttpServletResponse resp);
}
