package ru.y_lab.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.*;
import ru.y_lab.service.BookingService;
import ru.y_lab.swagger.API.BookingControllerAPI;

import java.util.List;

/**
 * Controller for managing bookings.
 * This class handles HTTP requests for creating, retrieving, updating, and deleting bookings.
 */
@Tag(name = "Booking API", description = "Operations about bookings")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController implements BookingControllerAPI {

    private final BookingService bookingService;

    /**
     * Adds a new booking.
     *
     * @param token the authentication token of the user making the request
     * @param request the booking request details
     * @return the created booking as a {@link BookingDTO}
     */
    @Override
    @PostMapping
    public ResponseEntity<BookingDTO> addBooking(@RequestHeader("Authorization") String token,
                                                 @RequestBody AddBookingRequestDTO request) {
        BookingDTO bookingDTO;
        bookingDTO = bookingService.addBooking(token, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDTO);
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param token the authentication token of the user making the request
     * @param bookingId the ID of the booking
     * @return the booking details as a {@link BookingWithOwnerResourceDTO}
     */
    @Override
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingWithOwnerResourceDTO> getBookingById(@RequestHeader("Authorization") String token,
                                                                      @PathVariable Long bookingId) {
        BookingWithOwnerResourceDTO booking = bookingService.getBookingById(token, bookingId);
        return ResponseEntity.ok(booking);
    }

    /**
     * Retrieves bookings for a specific user.
     *
     * @param token the authentication token of the user making the request
     * @return a list of bookings for the user as {@link BookingWithOwnerResourceDTO}
     */
    @Override
    @GetMapping("/user")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getUserBookings(@RequestHeader("Authorization") String token) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getUserBookings(token);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves all bookings.
     *
     * @param token the authentication token of the admin user making the request
     * @return a list of all bookings as {@link BookingWithOwnerResourceDTO}
     */
    @Override
    @GetMapping
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getAllBookings(@RequestHeader("Authorization") String token) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getAllBookings(token);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves bookings by a specific date.
     *
     * @param token the authentication token of the user making the request
     * @param date the date of the bookings in milliseconds since epoch
     * @return a list of bookings for the specified date as {@link BookingWithOwnerResourceDTO}
     */
    @Override
    @GetMapping("/date/{date}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByDate(@RequestHeader("Authorization") String token,
                                                                               @PathVariable Long date) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByDate(token, date);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves bookings for a specific user ID.
     *
     * @param token the authentication token of the admin user making the request
     * @param userId the ID of the user
     * @return a list of bookings for the specified user as {@link BookingWithOwnerResourceDTO}
     */
    @Override
    @GetMapping("/admin/{userId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByUserId(@RequestHeader("Authorization") String token,
                                                                                 @PathVariable Long userId) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves bookings for a specific resource ID.
     *
     * @param token the authentication token of the user making the request
     * @param resourceId the ID of the resource
     * @return a list of bookings for the specified resource as {@link BookingWithOwnerResourceDTO}
     */
    @Override
    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByResourceId(@RequestHeader("Authorization") String token,
                                                                                     @PathVariable Long resourceId) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByResourceId(token, resourceId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Retrieves available slots for a specific resource and date.
     *
     * @param token the authentication token of the user making the request
     * @param request the available slots request details
     * @return a list of available slots as {@link AvailableSlotDTO}
     */
    @Override
    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(@RequestHeader("Authorization") String token,
                                                                    @RequestBody AvailableSlotsRequestDTO request) {
        List<AvailableSlotDTO> availableSlots = bookingService.getAvailableSlots(token, request);
        return ResponseEntity.ok(availableSlots);
    }

    /**
     * Updates an existing booking.
     *
     * @param token the authentication token of the user making the request
     * @param bookingId the ID of the booking to be updated
     * @param request the update request details
     * @return the updated booking as a {@link BookingDTO}
     */
    @Override
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> updateBooking(@RequestHeader("Authorization") String token,
                                                    @PathVariable Long bookingId,
                                                    @RequestBody UpdateBookingRequestDTO request) {
        BookingDTO updatedBooking = bookingService.updateBooking(token, bookingId, request);
        return ResponseEntity.ok(updatedBooking);
    }

    /**
     * Deletes a booking by its ID.
     *
     * @param token the authentication token of the user making the request
     * @param bookingId the ID of the booking to be deleted
     * @return a response with HTTP status NO_CONTENT
     */
    @Override
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@RequestHeader("Authorization") String token,
                                              @PathVariable Long bookingId) {
        bookingService.deleteBooking(token, bookingId);
        return ResponseEntity.noContent().build();
    }
}
