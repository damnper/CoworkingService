package ru.y_lab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.exception.*;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.service.impl.BookingServiceImpl;
import ru.y_lab.util.InputReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BookingServiceImpl class.
 */
public class BookingServiceImplTest {
    @Mock
    private InputReader inputReader;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ResourceService resourceService;

    private BookingServiceImpl bookingService;

    /**
     * Sets up the test environment by initializing mocks and creating a new BookingServiceImpl instance.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(inputReader, bookingRepository, userService, resourceService);
    }

    /**
     * Test case for adding a booking successfully without any conflicts.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testAddBookingSuccessfully() throws Exception {
        String resourceId = "resource-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(userService.getCurrentUser()).thenReturn(new User("user-id", "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(resourceId, date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, "user-id", "Resource Name", "Workspace"));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Collections.emptyList());

        bookingService.addBooking();

        verify(bookingRepository).addBooking(any(Booking.class));
    }

    /**
     * Test case for adding a booking that conflicts with an existing booking.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testAddBookingConflict() throws Exception {
        String resourceId = "resource-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
        Booking existingBooking = new Booking(UUID.randomUUID().toString(), "user-id", resourceId, startDateTime.minusMinutes(30), endDateTime.plusMinutes(30));

        when(userService.getCurrentUser()).thenReturn(new User("user-id", "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(resourceId, date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, "user-id", "Resource Name", "Workspace"));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Collections.singletonList(existingBooking));

        BookingConflictException exception = assertThrows(BookingConflictException.class, () -> bookingService.addBooking());

        assertEquals("Booking conflict: The resource is already booked during this time.", exception.getMessage());
    }

    /**
     * Test case for cancelling a booking successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testCancelBookingSuccessfully() {
        String bookingId = "booking-id";
        User currentUser = new User("user-id", "user-name", "password", "USER");
        Booking booking = new Booking(bookingId, "user-id", "resource-id", LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(inputReader.readLine()).thenReturn(bookingId);
        when(bookingRepository.getBookingById(bookingId)).thenReturn(booking);

        bookingService.cancelBooking();

        verify(bookingRepository).deleteBooking(bookingId);
    }

    /**
     * Test case for cancelling a booking that does not exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testCancelBookingNotFound() {
        String bookingId = "booking-id";

        when(userService.getCurrentUser()).thenReturn(new User("user-id", "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(bookingId);
        when(bookingRepository.getBookingById(bookingId)).thenReturn(null);

        bookingService.cancelBooking();

        verify(bookingRepository, never()).deleteBooking(anyString());
    }

    /**
     * Test case for viewing bookings of the current user.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testViewUserBookings() throws Exception {
        User currentUser = new User("user-id", "user-name", "password", "USER");
        Resource resource = new Resource("resource-id", "user-id", "Resource Name", "Workspace");
        List<Booking> userBookings = List.of(
                new Booking(UUID.randomUUID().toString(), "user-id", resource.getId(), LocalDateTime.now(), LocalDateTime.now().plusHours(1))
        );

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId("user-id")).thenReturn(userBookings);
        when(userService.getUserById("user-id")).thenReturn(currentUser);
        when(resourceService.getResourceById(resource.getId())).thenReturn(resource);

        bookingService.viewUserBookings();

        verify(bookingRepository).getBookingsByUserId("user-id");
    }

    /**
     * Test case for viewing available booking slots for a resource.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testViewAvailableSlots() {
        String resourceId = "resource-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startOfDay = LocalTime.of(9, 0);
        List<Booking> bookings = List.of(
                new Booking(UUID.randomUUID().toString(), "user1", resourceId, LocalDateTime.of(date, startOfDay.plusHours(1)), LocalDateTime.of(date, startOfDay.plusHours(2))),
                new Booking(UUID.randomUUID().toString(), "user2", resourceId, LocalDateTime.of(date, startOfDay.plusHours(4)), LocalDateTime.of(date, startOfDay.plusHours(5)))
        );

        when(inputReader.readLine()).thenReturn(resourceId, date.toString());
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(bookings);

        bookingService.viewAvailableSlots();

        verify(bookingRepository).getBookingsByResourceId(resourceId);
    }

    /**
     * Test case for updating a booking successfully without conflicts.
     *
     * @throws BookingConflictException if a booking conflict occurs
     */
    @Test
    public void testUpdateBookingSuccessfully() throws BookingConflictException {
        String bookingId = "booking-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime newStartTime = LocalTime.of(12, 0);
        LocalTime newEndTime = LocalTime.of(13, 0);
        Booking existingBooking = new Booking(bookingId, "user-id", "resource-id", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));

        when(userService.getCurrentUser()).thenReturn(new User("user-id", "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(bookingId, date.toString(), newStartTime.toString(), newEndTime.toString());
        when(bookingRepository.getBookingById(bookingId)).thenReturn(existingBooking);
        when(bookingRepository.getBookingsByResourceId("resource-id")).thenReturn(Collections.singletonList(existingBooking));

        bookingService.updateBooking();

        verify(bookingRepository).updateBooking(any(Booking.class));
    }

    /**
     * Test case for updating a booking that conflicts with another booking.
     */
    @Test
    public void testUpdateBookingConflict() {
        String bookingId = "booking-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime newStartTime = LocalTime.of(12, 0);
        LocalTime newEndTime = LocalTime.of(13, 0);
        LocalDateTime newStartDateTime = LocalDateTime.of(date, newStartTime);
        LocalDateTime newEndDateTime = LocalDateTime.of(date, newEndTime);
        Booking existingBooking = new Booking(bookingId, "user-id", "resource-id", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        Booking conflictingBooking = new Booking(UUID.randomUUID().toString(), "user-id", "resource-id", newStartDateTime.minusMinutes(30), newEndDateTime.plusMinutes(30));

        when(userService.getCurrentUser()).thenReturn(new User("user-id", "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(bookingId, date.toString(), newStartTime.toString(), newEndTime.toString());
        when(bookingRepository.getBookingById(bookingId)).thenReturn(existingBooking);
        when(bookingRepository.getBookingsByResourceId("resource-id")).thenReturn(List.of(existingBooking, conflictingBooking));

        BookingConflictException exception = assertThrows(BookingConflictException.class, () -> bookingService.updateBooking());

        assertEquals("Booking conflict: The resource is already booked during this time.", exception.getMessage());
    }

    /**
     * Test case for updating a booking that does not exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testUpdateBookingNotFound() {
        String bookingId = "booking-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime newStartTime = LocalTime.of(12, 0);
        LocalTime newEndTime = LocalTime.of(11, 0);

        when(userService.getCurrentUser()).thenReturn(new User("user-id", "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(bookingId, date.toString(), newStartTime.toString(), newEndTime.toString());

        assertThrows(BookingNotFoundException.class, () -> bookingService.updateBooking());
    }

    /**
     * Test case for getting booking date time successfully when adding a new booking.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testGetBookingDateTime_SuccessfulBooking() throws ResourceNotFoundException {
        String resourceId = "resource-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(inputReader.readLine()).thenReturn(date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, "user-id", "Resource Name", "Workspace"));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Collections.emptyList());

        LocalDateTime[] result = bookingService.getBookingDateTime(resourceId, false);

        assertNotNull(result);
        assertEquals(date.atTime(startTime), result[0]);
        assertEquals(date.atTime(endTime), result[1]);
    }

    /**
     * Test case for getting booking date time with invalid time format.
     */
    @Test
    public void testGetBookingDateTime_InvalidTimeFormat() {
        String resourceId = "resource-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime endTime = LocalTime.of(11, 0);

        when(inputReader.readLine()).thenReturn(date.toString(), "invalid-time", endTime.toString());

        assertThrows(InvalidBookingTimeException.class, () -> bookingService.getBookingDateTime(resourceId, false));
    }

    /**
     * Test case for getting booking date time when viewing available slots.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testGetBookingDateTime_ViewAvailableSlots() throws ResourceNotFoundException {
        String resourceId = "resource-id";
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        List<Booking> bookings = Collections.emptyList();

        when(inputReader.readLine()).thenReturn(date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, "user-id", "Resource Name", "Workspace"));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(bookings);

        LocalDateTime[] result = bookingService.getBookingDateTime(resourceId, true);

        assertNotNull(result);
        assertEquals(date.atTime(startTime), result[0]);
        assertEquals(date.atTime(endTime), result[1]);
        verify(bookingRepository).getBookingsByResourceId(resourceId);
    }

    /**
     * Test case for managing bookings when user is not logged in.
     */
    @Test
    public void testManageBookings_UserNotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> bookingService.manageBookings());
    }

    /**
     * Test case for managing bookings: adding a new booking.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testManageBookings_AddBooking() throws Exception {
        User currentUser = new User("user-id", "username", "password", "USER");
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(inputReader.readLine()).thenReturn("1", "resource-id", date.toString(), startTime.toString(), endTime.toString(), "0");

        bookingService.manageBookings();

        verify(inputReader, times(6)).readLine(); // 5 times for input + 1 time for exiting loop
        verify(resourceService, times(1)).viewResources();
    }

    /**
     * Test case for managing bookings: cancelling a booking.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testManageBookings_CancelBooking() throws Exception {
        User currentUser = new User("user-id", "username", "password", "USER");
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 20, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 20,11, 0);
        Booking booking = new Booking("booking-id", "user-id", "resource-id", startTime, endTime);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.getBookingById(anyString())).thenReturn(booking);
        when(inputReader.readLine()).thenReturn("2", "booking-id", "0");

        bookingService.manageBookings();

        verify(inputReader, times(3)).readLine(); // 2 time for input + 1 time for exiting loop
    }

    /**
     * Test case for managing bookings: viewing user's bookings.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testManageBookings_ViewUserBookings() throws Exception {
        User currentUser = new User("user-id", "username", "password", "USER");
        Booking booking1 = new Booking("booking-id-1", "user-id", "resource-id-1", LocalDateTime.of(2023, 6, 23, 10, 0), LocalDateTime.of(2023, 6, 23, 11, 0));
        Booking booking2 = new Booking("booking-id-2", "user-id", "resource-id-2", LocalDateTime.of(2023, 6, 24, 12, 0), LocalDateTime.of(2023, 6, 24, 13, 0));
        List<Booking> userBookings = Arrays.asList(booking1, booking2);
        Resource resource = new Resource("resource-id", "user-id", "name", "type");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId(anyString())).thenReturn(userBookings);
        when(userService.getUserById(anyString())).thenReturn(currentUser);
        when(resourceService.getResourceById(anyString())).thenReturn(resource);
        when(inputReader.readLine()).thenReturn("3", "0");

        bookingService.manageBookings();

        verify(inputReader, times(2)).readLine(); // 1 time for input + 1 time for exiting loop
    }

    /**
     * Test case for managing bookings: updating a booking.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testManageBookings_UpdateBooking() throws Exception {
        User currentUser = new User("user-id", "username", "password", "USER");
        Booking booking = new Booking("booking-id", "user-id", "resource-id", LocalDateTime.of(2023, 6, 23, 9, 0), LocalDateTime.of(2023, 6, 23, 10, 0));
        Booking booking1 = new Booking("booking-id-1", "user-id", "resource-id-1", LocalDateTime.of(2023, 6, 23, 10, 0), LocalDateTime.of(2023, 6, 23, 11, 0));
        Booking booking2 = new Booking("booking-id-2", "user-id", "resource-id-2", LocalDateTime.of(2023, 6, 24, 12, 0), LocalDateTime.of(2023, 6, 24, 13, 0));
        List<Booking> userBookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.getBookingById(anyString())).thenReturn(booking);
        when(bookingRepository.getBookingsByResourceId(anyString())).thenReturn(userBookings);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        doNothing().when(bookingRepository).updateBooking(booking);
        when(inputReader.readLine()).thenReturn("4", "booking-id", "2023-06-23", "09:00", "10:00", "0");

        bookingService.manageBookings();

        verify(inputReader, times(6)).readLine(); // 5 time for input + 1 time for exiting loop
    }

    /**
     * Test case for managing bookings: viewing available booking slots.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testManageBookings_ViewAvailableSlots() throws Exception {
        User currentUser = new User("user-id", "username", "password", "USER");
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(inputReader.readLine()).thenReturn("5", "resource-id", "2023-06-20", "0");

        bookingService.manageBookings();

        verify(inputReader, times(4)).readLine(); // 1 time for input + 1 time for exiting loop
    }

    /**
     * Test case for filtering bookings by date.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testFilterBookings_ByDate_Success() throws Exception {
        User currentUser = new User("user-id", "username", "password", "USER");
        Resource resource = new Resource("resource-id", "user-id", "name", "type");
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getUserById(anyString())).thenReturn(currentUser);
        when(resourceService.getResourceById(anyString())).thenReturn(resource);

        when(inputReader.readLine()).thenReturn("1", "2023-06-23");

        Booking booking1 = new Booking("booking-id-1", "user-id", "resource-id-1", LocalDateTime.of(2023, 6, 23, 10, 0), LocalDateTime.of(2023, 6, 23, 11, 0));
        Booking booking2 = new Booking("booking-id-2", "user-id", "resource-id-2", LocalDateTime.of(2023, 6, 23, 12, 0), LocalDateTime.of(2023, 6, 23, 13, 0));
        List<Booking> bookingsByDate = Arrays.asList(booking1, booking2);
        when(bookingRepository.getAllBookings()).thenReturn(bookingsByDate);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        bookingService.filterBookings();

        verify(userService, times(1)).getCurrentUser();
        verify(inputReader, times(2)).readLine(); // Verify twice for choice and date
        verify(resourceService, times(4)).getResourceById(anyString()); // Verify for each resource ID

    }
}
