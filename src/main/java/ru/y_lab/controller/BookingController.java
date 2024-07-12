package ru.y_lab.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.*;
import ru.y_lab.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> addBooking(@RequestBody AddBookingRequestDTO request, HttpServletRequest httpRequest) {
        BookingDTO bookingDTO;
        try {
            bookingDTO = bookingService.addBooking(request, httpRequest);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDTO);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingWithOwnerResourceDTO> getBookingById(@PathVariable Long bookingId, HttpServletRequest httpRequest) {
        BookingWithOwnerResourceDTO booking = bookingService.getBookingById(bookingId, httpRequest);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getUserBookings(@PathVariable Long userId, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getUserBookings(userId, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getAllBookings(HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getAllBookings(httpRequest);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByDate(@PathVariable Long date, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByDate(date, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user-id/{userId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByUserId(@PathVariable Long userId, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByUserId(userId, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/resource-id/{resourceId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByResourceId(@PathVariable Long resourceId, HttpServletRequest httpRequest) {
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByResourceId(resourceId, httpRequest);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(@RequestBody AvailableSlotsRequestDTO request, HttpServletRequest httpRequest) {
        List<AvailableSlotDTO> availableSlots = bookingService.getAvailableSlots(request, httpRequest);
        return ResponseEntity.ok(availableSlots);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingRequestDTO request, HttpServletRequest httpRequest) {
        BookingDTO updatedBooking = bookingService.updateBooking(bookingId, request, httpRequest);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId, HttpServletRequest httpRequest) {
        bookingService.deleteBooking(bookingId, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
