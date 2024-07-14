package ru.y_lab.controller;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.y_lab.dto.*;
import ru.y_lab.exception.AuthorizationException;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("BookingController Test")
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Add Booking")
    void addBooking() {
        AddBookingRequestDTO addBookingRequest = new AddBookingRequestDTO(1L, 1721469600000L, 1721473200000L);
        BookingDTO bookingDTO = new BookingDTO(1L, 1L, 1L, "2024-07-20T10:00", "2024-07-20T12:00");

        when(bookingService.addBooking(addBookingRequest, httpRequest)).thenReturn(bookingDTO);

        ResponseEntity<BookingDTO> response = bookingController.addBooking(addBookingRequest, httpRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(bookingDTO, response.getBody());
        verify(bookingService).addBooking(addBookingRequest, httpRequest);
    }

    @Test
    @DisplayName("Add Booking - Booking Conflict")
    void addBookingConflict() {
        AddBookingRequestDTO addBookingRequest = new AddBookingRequestDTO(1L, 1721469600000L, 1721473200000L);

        when(bookingService.addBooking(addBookingRequest, httpRequest)).thenThrow(new BookingConflictException("The resource is already booked for the specified time period."));

        Exception exception = assertThrows(BookingConflictException.class, () -> bookingController.addBooking(addBookingRequest, httpRequest));

        assertEquals("The resource is already booked for the specified time period.", exception.getMessage());
        verify(bookingService).addBooking(addBookingRequest, httpRequest);
    }

    @Test
    @DisplayName("Get Booking By ID")
    void getBookingById() {
        Long bookingId = 1L;
        BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource name", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00");

        when(bookingService.getBookingById(bookingId, httpRequest)).thenReturn(bookingWithOwnerResourceDTO);

        ResponseEntity<BookingWithOwnerResourceDTO> response = bookingController.getBookingById(bookingId, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingWithOwnerResourceDTO, response.getBody());
        verify(bookingService).getBookingById(bookingId, httpRequest);
    }

    @Test
    @DisplayName("Get Booking By ID - Booking Not Found")
    void getBookingByIdNotFound() {
        Long bookingId = 1L;

        when(bookingService.getBookingById(bookingId, httpRequest)).thenThrow(new BookingNotFoundException("The requested booking was not found."));

        Exception exception = assertThrows(BookingNotFoundException.class, () -> bookingController.getBookingById(bookingId, httpRequest));

        assertEquals("The requested booking was not found.", exception.getMessage());
        verify(bookingService).getBookingById(bookingId, httpRequest);
    }

    @Test
    @DisplayName("Get User Bookings")
    void getUserBookings() {
        Long userId = 1L;
        List<BookingWithOwnerResourceDTO> bookings = List.of(
                new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource name", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00")
        );

        when(bookingService.getUserBookings(userId, httpRequest)).thenReturn(bookings);

        ResponseEntity<List<BookingWithOwnerResourceDTO>> response = bookingController.getUserBookings(userId, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        verify(bookingService).getUserBookings(userId, httpRequest);
    }

    @Test
    @DisplayName("Get User Bookings - Unauthorized")
    void getUserBookingsUnauthorized() {
        Long userId = 1L;

        when(bookingService.getUserBookings(userId, httpRequest)).thenThrow(new AuthorizationException("Not authorized"));

        Exception exception = assertThrows(AuthorizationException.class, () -> bookingController.getUserBookings(userId, httpRequest));

        assertEquals("Not authorized", exception.getMessage());
        verify(bookingService).getUserBookings(userId, httpRequest);
    }

    @Test
    @DisplayName("Get All Bookings")
    void getAllBookings() {
        List<BookingWithOwnerResourceDTO> bookings = List.of(
                new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource name", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00")
        );

        when(bookingService.getAllBookings(httpRequest)).thenReturn(bookings);

        ResponseEntity<List<BookingWithOwnerResourceDTO>> response = bookingController.getAllBookings(httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        verify(bookingService).getAllBookings(httpRequest);
    }

    @Test
    @DisplayName("Get All Bookings - Unauthorized")
    void getAllBookingsUnauthorized() {
        when(bookingService.getAllBookings(httpRequest)).thenThrow(new AuthorizationException("Not authorized"));

        Exception exception = assertThrows(AuthorizationException.class, () -> bookingController.getAllBookings(httpRequest));

        assertEquals("Not authorized", exception.getMessage());
        verify(bookingService).getAllBookings(httpRequest);
    }

    @Test
    @DisplayName("Get Bookings By Date")
    void getBookingsByDate() {
        Long date = 20230712L;
        List<BookingWithOwnerResourceDTO> bookings = List.of(
                new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource name", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00")
        );

        when(bookingService.getBookingsByDate(date, httpRequest)).thenReturn(bookings);

        ResponseEntity<List<BookingWithOwnerResourceDTO>> response = bookingController.getBookingsByDate(date, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        verify(bookingService).getBookingsByDate(date, httpRequest);
    }

    @Test
    @DisplayName("Get Bookings By Date - Booking Not Found")
    void getBookingsByDateNotFound() {
        Long date = 20230712L;

        when(bookingService.getBookingsByDate(date, httpRequest)).thenThrow(new BookingNotFoundException("No bookings were found for the specified date."));

        Exception exception = assertThrows(BookingNotFoundException.class, () -> bookingController.getBookingsByDate(date, httpRequest));

        assertEquals("No bookings were found for the specified date.", exception.getMessage());
        verify(bookingService).getBookingsByDate(date, httpRequest);
    }

    @Test
    @DisplayName("Get Bookings By User ID")
    void getBookingsByUserId() {
        Long userId = 1L;
        List<BookingWithOwnerResourceDTO> bookings = List.of(
                new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource name", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00")
        );

        when(bookingService.getBookingsByUserId(userId, httpRequest)).thenReturn(bookings);

        ResponseEntity<List<BookingWithOwnerResourceDTO>> response = bookingController.getBookingsByUserId(userId, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        verify(bookingService).getBookingsByUserId(userId, httpRequest);
    }

    @Test
    @DisplayName("Get Bookings By Resource ID")
    void getBookingsByResourceId() {
        Long resourceId = 1L;
        List<BookingWithOwnerResourceDTO> bookings = List.of(
                new BookingWithOwnerResourceDTO(1L, 1L, 1L, "user", "Resource name", "CONFERENCE_ROOM", "2024-07-20", "10:00", "12:00")
        );

        when(bookingService.getBookingsByResourceId(resourceId, httpRequest)).thenReturn(bookings);

        ResponseEntity<List<BookingWithOwnerResourceDTO>> response = bookingController.getBookingsByResourceId(resourceId, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        verify(bookingService).getBookingsByResourceId(resourceId, httpRequest);
    }

    @Test
    @DisplayName("Get Bookings By Resource ID - Booking Not Found")
    void getBookingsByResourceIdNotFound() {
        Long resourceId = 1L;

        when(bookingService.getBookingsByResourceId(resourceId, httpRequest)).thenThrow(new BookingNotFoundException("No bookings were found for the specified resource."));

        Exception exception = assertThrows(BookingNotFoundException.class, () -> bookingController.getBookingsByResourceId(resourceId, httpRequest));

        assertEquals("No bookings were found for the specified resource.", exception.getMessage());
        verify(bookingService).getBookingsByResourceId(resourceId, httpRequest);
    }

    @Test
    @DisplayName("Get Available Slots")
    void getAvailableSlots() {
        AvailableSlotsRequestDTO request = new AvailableSlotsRequestDTO(1L, 20230712L);
        List<AvailableSlotDTO> availableSlots = List.of(
                new AvailableSlotDTO(1, "10:00", "11:00")
        );

        when(bookingService.getAvailableSlots(request, httpRequest)).thenReturn(availableSlots);

        ResponseEntity<List<AvailableSlotDTO>> response = bookingController.getAvailableSlots(request, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availableSlots, response.getBody());
        verify(bookingService).getAvailableSlots(request, httpRequest);
    }

    @Test
    @DisplayName("Update Booking")
    void updateBooking() {
        Long bookingId = 1L;
        UpdateBookingRequestDTO updateRequest = new UpdateBookingRequestDTO(1721469600000L, 1721473200000L);
        BookingDTO bookingDTO = new BookingDTO(1L, 1L, 1L, "2023-07-12T14:00", "2023-07-12T16:00");

        when(bookingService.updateBooking(bookingId, updateRequest, httpRequest)).thenReturn(bookingDTO);

        ResponseEntity<BookingDTO> response = bookingController.updateBooking(bookingId, updateRequest, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookingDTO, response.getBody());
        verify(bookingService).updateBooking(bookingId, updateRequest, httpRequest);
    }

    @Test
    @DisplayName("Update Booking - Not Found")
    void updateBookingNotFound() {
        Long bookingId = 1L;
        UpdateBookingRequestDTO updateRequest = new UpdateBookingRequestDTO(1721469600000L, 1721473200000L);

        when(bookingService.updateBooking(bookingId, updateRequest, httpRequest)).thenThrow(new BookingNotFoundException("The booking to be updated was not found."));

        Exception exception = assertThrows(BookingNotFoundException.class, () -> bookingController.updateBooking(bookingId, updateRequest, httpRequest));

        assertEquals("The booking to be updated was not found.", exception.getMessage());
        verify(bookingService).updateBooking(bookingId, updateRequest, httpRequest);
    }

    @Test
    @DisplayName("Update Booking - Conflict")
    void updateBookingConflict() {
        Long bookingId = 1L;
        UpdateBookingRequestDTO updateRequest = new UpdateBookingRequestDTO(1721469600000L, 1721473200000L);

        when(bookingService.updateBooking(bookingId, updateRequest, httpRequest)).thenThrow(new BookingConflictException("The resource is already booked for the specified time period."));

        Exception exception = assertThrows(BookingConflictException.class, () -> bookingController.updateBooking(bookingId, updateRequest, httpRequest));

        assertEquals("The resource is already booked for the specified time period.", exception.getMessage());
        verify(bookingService).updateBooking(bookingId, updateRequest, httpRequest);
    }

    @Test
    @DisplayName("Delete Booking")
    void deleteBooking() {
        Long bookingId = 1L;

        doNothing().when(bookingService).deleteBooking(bookingId, httpRequest);

        ResponseEntity<Void> response = bookingController.deleteBooking(bookingId, httpRequest);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookingService).deleteBooking(bookingId, httpRequest);
    }

    @Test
    @DisplayName("Delete Booking - Not Found")
    void deleteBookingNotFound() {
        Long bookingId = 1L;

        doThrow(new BookingNotFoundException("The booking to be deleted was not found.")).when(bookingService).deleteBooking(bookingId, httpRequest);

        Exception exception = assertThrows(BookingNotFoundException.class, () -> bookingController.deleteBooking(bookingId, httpRequest));

        assertEquals("The booking to be deleted was not found.", exception.getMessage());
        verify(bookingService).deleteBooking(bookingId, httpRequest);
    }

    @Test
    @DisplayName("Delete Booking - Unauthorized")
    void deleteBookingUnauthorized() {
        Long bookingId = 1L;

        doThrow(new AuthorizationException("Not authorized")).when(bookingService).deleteBooking(bookingId, httpRequest);

        Exception exception = assertThrows(AuthorizationException.class, () -> bookingController.deleteBooking(bookingId, httpRequest));

        assertEquals("Not authorized", exception.getMessage());
        verify(bookingService).deleteBooking(bookingId, httpRequest);
    }
}
