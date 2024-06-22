package ru.y_lab.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingRepositoryTest {

    private BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {
        bookingRepository = new BookingRepository();
    }

    @Test
    public void testAddBooking() {
        Booking booking = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking);

        Booking retrievedBooking = bookingRepository.getBookingById("1");
        assertNotNull(retrievedBooking);
        assertEquals(booking, retrievedBooking);
    }

    @Test
    public void testGetBookingById() throws BookingNotFoundException {
        Booking booking = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking);

        Booking retrievedBooking = bookingRepository.getBookingById("1");
        assertNotNull(retrievedBooking);
        assertEquals(booking, retrievedBooking);
    }

    @Test
    public void testGetBookingByIdNotFound() {
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.getBookingById("nonexistent_id"));
    }

    @Test
    public void testUpdateBooking() throws BookingNotFoundException {
        Booking booking = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking);

        Booking updatedBooking = new Booking("1", "user1", "resource2", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.updateBooking(updatedBooking);

        Booking retrievedBooking = bookingRepository.getBookingById("1");
        assertNotNull(retrievedBooking);
        assertEquals(updatedBooking, retrievedBooking);
    }

    @Test
    public void testUpdateNonExistentBooking() {
        Booking nonExistentBooking = new Booking("nonexistent_id", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.updateBooking(nonExistentBooking));
    }

    @Test
    public void testDeleteBooking() throws BookingNotFoundException {
        Booking booking = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking);

        bookingRepository.deleteBooking("1");
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.getBookingById("1"));
    }

    @Test
    public void testDeleteNonExistentBooking() {
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.deleteBooking("nonexistent_id"));
    }

    @Test
    public void testGetAllBookings() {
        Booking booking1 = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Booking booking2 = new Booking("2", "user2", "resource2", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking1);
        bookingRepository.addBooking(booking2);

        List<Booking> allBookings = bookingRepository.getAllBookings();
        assertEquals(2, allBookings.size());
        assertTrue(allBookings.contains(booking1));
        assertTrue(allBookings.contains(booking2));
    }

    @Test
    public void testGetBookingsByUserId() {
        Booking booking1 = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Booking booking2 = new Booking("2", "user1", "resource2", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking1);
        bookingRepository.addBooking(booking2);

        List<Booking> user1Bookings = bookingRepository.getBookingsByUserId("user1");
        assertEquals(2, user1Bookings.size());
        assertTrue(user1Bookings.contains(booking1));
        assertTrue(user1Bookings.contains(booking2));
    }

    @Test
    public void testGetBookingsByResourceId() {
        Booking booking1 = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Booking booking2 = new Booking("2", "user2", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking1);
        bookingRepository.addBooking(booking2);

        List<Booking> resource1Bookings = bookingRepository.getBookingsByResourceId("resource1");
        assertEquals(2, resource1Bookings.size());
        assertTrue(resource1Bookings.contains(booking1));
        assertTrue(resource1Bookings.contains(booking2));
    }

}