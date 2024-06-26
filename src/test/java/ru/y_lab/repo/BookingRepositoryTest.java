package ru.y_lab.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BookingRepository class.
 */
@DisplayName("Unit Tests for BookingRepositoryTest")
public class BookingRepositoryTest {

    private BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {
        bookingRepository = new BookingRepository();
    }
    /**
     * Test case for adding a booking to the repository.
     */
    @Test
    public void testAddBooking() {
        Booking booking = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking);

        Booking retrievedBooking = bookingRepository.getBookingById("1");
        assertNotNull(retrievedBooking);
        assertEquals(booking, retrievedBooking);
    }

    /**
     * Test case for retrieving a booking by ID.
     *
     * @throws BookingNotFoundException if the booking is not found
     */
    @Test
    public void testGetBookingById() throws BookingNotFoundException {
        Booking booking = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking);

        Booking retrievedBooking = bookingRepository.getBookingById("1");
        assertNotNull(retrievedBooking);
        assertEquals(booking, retrievedBooking);
    }

    /**
     * Test case for retrieving a non-existent booking by ID, expecting an exception.
     */
    @Test
    public void testGetBookingByIdNotFound() {
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.getBookingById("nonexistent_id"));
    }

    /**
     * Test case for updating an existing booking.
     *
     * @throws BookingNotFoundException if the booking to update is not found
     */
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

    /**
     * Test case for updating a non-existent booking, expecting an exception.
     */
    @Test
    public void testUpdateNonExistentBooking() {
        Booking nonExistentBooking = new Booking("nonexistent_id", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.updateBooking(nonExistentBooking));
    }

    /**
     * Test case for deleting an existing booking.
     *
     * @throws BookingNotFoundException if the booking to delete is not found
     */
    @Test
    public void testDeleteBooking() throws BookingNotFoundException {
        Booking booking = new Booking("1", "user1", "resource1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        bookingRepository.addBooking(booking);

        bookingRepository.deleteBooking("1");
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.getBookingById("1"));
    }

    /**
     * Test case for deleting a non-existent booking, expecting an exception.
     */
    @Test
    public void testDeleteNonExistentBooking() {
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.deleteBooking("nonexistent_id"));
    }

    /**
     * Test case for retrieving all bookings from the repository.
     */
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

    /**
     * Test case for retrieving bookings by user ID.
     */
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

    /**
     * Test case for retrieving bookings by resource ID.
     */
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