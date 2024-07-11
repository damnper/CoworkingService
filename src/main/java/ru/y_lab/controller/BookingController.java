package ru.y_lab.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.y_lab.dto.*;
import ru.y_lab.service.BookingService;
import ru.y_lab.util.AuthenticationUtil;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final AuthenticationUtil authUtil;

    @PostMapping
    public ResponseEntity<BookingDTO> addBooking(@RequestBody AddBookingRequestDTO request, HttpServletRequest req) {
        UserDTO currentUser = authUtil.authenticate(req, null);
        BookingDTO bookingDTO = bookingService.addBooking(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDTO);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingWithOwnerResourceDTO> getBookingById(@PathVariable Long bookingId, HttpServletRequest req) {
        authUtil.authenticate(req, null);
        BookingWithOwnerResourceDTO booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getUserBookings(@PathVariable Long userId, HttpServletRequest req) {
        authUtil.authenticate(req, null);
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getAllBookings(HttpServletRequest req) {
        authUtil.authenticate(req, "ADMIN");
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByDate(@PathVariable Long date, HttpServletRequest req) {
        authUtil.authenticate(req, "ADMIN");
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByDate(date);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user-id/{userId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByUserId(@PathVariable Long userId, HttpServletRequest req) {
        authUtil.authenticate(req, "ADMIN");
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/resource-id/{resourceId}")
    public ResponseEntity<List<BookingWithOwnerResourceDTO>> getBookingsByResourceId(@PathVariable Long resourceId, HttpServletRequest req) {
        authUtil.authenticate(req, "ADMIN");
        List<BookingWithOwnerResourceDTO> bookings = bookingService.getBookingsByResourceId(resourceId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(@RequestBody AvailableSlotsRequestDTO request, HttpServletRequest req) {
        authUtil.authenticate(req, null);
        List<AvailableSlotDTO> availableSlots = bookingService.getAvailableSlots(request);
        return ResponseEntity.ok(availableSlots);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingRequestDTO request, HttpServletRequest req) {
        UserDTO currentUser = authUtil.authenticate(req, null);
        BookingDTO updatedBooking = bookingService.updateBooking(bookingId, request, currentUser);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId, HttpServletRequest req) {
        UserDTO currentUser = authUtil.authenticate(req, null);
        bookingService.deleteBooking(bookingId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
