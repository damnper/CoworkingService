package ru.y_lab.service;

import ru.y_lab.dto.*;

import java.util.List;

/**
 * The BookingService interface defines methods for managing bookings.
 */
public interface BookingService {

    BookingDTO addBooking(AddBookingRequestDTO requestDTO, UserDTO currentUser);

    BookingWithOwnerResourceDTO getBookingById(Long bookingId);

    List<BookingWithOwnerResourceDTO> getUserBookings(Long userId);

    List<BookingWithOwnerResourceDTO> getAllBookings();

    List<BookingWithOwnerResourceDTO> getBookingsByDate(Long date);

    List<BookingWithOwnerResourceDTO> getBookingsByUserId(Long userId);

    List<BookingWithOwnerResourceDTO> getBookingsByResourceId(Long resourceId);

    List<AvailableSlotDTO> getAvailableSlots(AvailableSlotsRequestDTO request);

    BookingDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request, UserDTO currentUser);

    void deleteBooking(Long bookingId, UserDTO currentUser);
}
