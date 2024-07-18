package ru.y_lab.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.*;
import ru.y_lab.service.BookingService;

import java.util.List;

/**
 * Controller for managing bookings.
 * This class handles HTTP requests for creating, retrieving, updating, and deleting bookings.
 */
@Tag(name = "Booking API", description = "Operations about bookings")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Adds a new booking.
     *
     * @param request the booking request details
     * @param httpRequest the HTTP request
     * @return the created booking as a {@link BookingDTO}
     */
    @PostMapping
    public ResponseEntity<BookingDTO> addBooking(@RequestBody AddBookingRequestDTO request, HttpServletRequest httpRequest) {
        BookingDTO bookingDTO;
        bookingDTO = bookingService.addBooking(request, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDTO);
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId the ID of the booking
     * @param httpRequest the HTTP request
     * @return the booking details as a {@link BookingWithOwnerResourceDTO}
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingWithOwnerResourceDTO> getBookingById(@PathVariable Long bookingId, HttpServletRequest httpRequest) {
        BookingWithOwnerResourceDTO booking = bookingService.getBookingById(bookingId, httpRequest);
        return ResponseEntity.ok(booking);
    }

    /**
     * Retrieves bookings for a specific user.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request
     * @return a list of bookings for the user as {@link BookingWithOwnerResourceDTO}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getUserBookings(@PathVariable Long userId, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getUserBookings(userId, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves all bookings.
     *
     * @param httpRequest the HTTP request
     * @return a list of all bookings as {@link BookingWithOwnerResourceDTO}
     */
    @GetMapping
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getAllBookings(HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getAllBookings(httpRequest);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves bookings by a specific date.
     *
     * @param date the date of the bookings
     * @param httpRequest the HTTP request
     * @return a list of bookings for the specified date as {@link BookingWithOwnerResourceDTO}
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByDate(@PathVariable Long date, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByDate(date, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves bookings for a specific user ID.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request
     * @return a list of bookings for the specified user as {@link BookingWithOwnerResourceDTO}
     */
    @GetMapping("/user-id/{userId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByUserId(@PathVariable Long userId, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByUserId(userId, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves bookings for a specific resource ID.
     *
     * @param resourceId the ID of the resource
     * @param httpRequest the HTTP request
     * @return a list of bookings for the specified resource as {@link BookingWithOwnerResourceDTO}
     */
    @GetMapping("/resource-id/{resourceId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByResourceId(@PathVariable Long resourceId, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByResourceId(resourceId, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves available slots for a specific resource and date.
     *
     * @param request the available slots request details
     * @param httpRequest the HTTP request
     * @return a list of available slots as {@link AvailableSlotDTO}
     */
    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(@RequestBody AvailableSlotsRequestDTO request, HttpServletRequest httpRequest) {
        List<AvailableSlotDTO> availableSlots = bookingService.getAvailableSlots(request, httpRequest);
        return ResponseEntity.ok(availableSlots);
    }

    /**
     * Updates an existing booking.
     *
     * @param bookingId the ID of the booking to be updated
     * @param request the update request details
     * @param httpRequest the HTTP request
     * @return the updated booking as a {@link BookingDTO}
     */
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingRequestDTO request, HttpServletRequest httpRequest) {
        BookingDTO updatedBooking = bookingService.updateBooking(bookingId, request, httpRequest);
        return ResponseEntity.ok(updatedBooking);
    }

    /**
     * Deletes a booking by its ID.
     *
     * @param bookingId the ID of the booking to be deleted
     * @param httpRequest the HTTP request
     * @return a response with HTTP status NO_CONTENT
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId, HttpServletRequest httpRequest) {
        bookingService.deleteBooking(bookingId, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
