package ru.y_lab.repo;


import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.model.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The BookingRepository class provides methods to manage bookings.
 * It stores booking data in a HashMap and supports CRUD operations.
 */
public class BookingRepository {

    private final Map<String, Booking> bookings = new HashMap<>();

    /**
     * Adds a new booking to the repository.
     * @param booking the booking to be added
     */
    public void addBooking(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    /**
     * Retrieves a booking by its ID.
     * @param id the ID of the booking
     * @return the booking with the specified ID, or null if not found
     */
    public Booking getBookingById(String id) {
        Booking booking = bookings.get(id);
        if (booking == null) {
            throw new BookingNotFoundException("Booking with ID " + id + " not found");
        }
        return booking;
    }

    /**
     * Updates an existing booking in the repository.
     * @param booking the booking with updated information
     */
    public void updateBooking(Booking booking) {
        if (!bookings.containsKey(booking.getId())) {
            throw new BookingNotFoundException("Booking with ID " + booking.getId() + " not found");
        }
        bookings.put(booking.getId(), booking);
    }

    /**
     * Deletes a booking from the repository by its ID.
     * @param id the ID of the booking to be deleted
     */
    public void deleteBooking(String id) {
        if (bookings.remove(id) == null) {
            throw new BookingNotFoundException("Booking with ID " + id + " not found");
        }
    }

    /**
     * Retrieves all bookings from the repository.
     * @return a list of all bookings
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    /**
     * Retrieves bookings by user ID.
     * @param userId the ID of the user
     * @return a list of bookings for the specified user
     */
    public List<Booking> getBookingsByUserId(String userId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getUserId().equals(userId)) {
                result.add(booking);
            }
        }
        return result;
    }

    /**
     * Retrieves bookings by resource ID.
     * @param resourceId the ID of the resource
     * @return a list of bookings for the specified resource
     */
    public List<Booking> getBookingsByResourceId(String resourceId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getResourceId().equals(resourceId)) {
                result.add(booking);
            }
        }
        return result;
    }
}
