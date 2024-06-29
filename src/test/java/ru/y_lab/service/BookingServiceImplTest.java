package ru.y_lab.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.service.impl.BookingServiceImpl;
import ru.y_lab.ui.BookingUI;
import ru.y_lab.util.InputReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.y_lab.constants.ColorTextCodes.*;

/**
 * Unit tests for the BookingServiceImpl class.
 */
@DisplayName("Unit Tests for BookingServiceImplTest")
public class BookingServiceImplTest {
    @Mock
    private InputReader inputReader;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private BookingUI bookingUI;

    private BookingServiceImpl bookingService;

    /**
     * Sets up the test environment by initializing mocks and creating a new BookingServiceImpl instance.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(inputReader, bookingRepository, userService, resourceService, bookingUI);
    }

    /**
     * Test case for adding a booking successfully without any conflicts.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testAddBookingSuccessfully() throws ResourceNotFoundException {
        Long resourceId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(userService.getCurrentUser()).thenReturn(new User(1L, "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(String.valueOf(resourceId), date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, 1L, "Resource Name", "Workspace"));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Collections.emptyList());

        bookingService.addBooking();

        verify(bookingRepository).addBooking(any(Booking.class));
    }

    /**
     * Test case for adding a booking that conflicts with an existing booking.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testAddBookingConflict() throws ResourceNotFoundException {
        Long resourceId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
        Booking existingBooking = new Booking(1L, 1L, resourceId, startDateTime.minusMinutes(30), endDateTime.plusMinutes(30));

        when(userService.getCurrentUser()).thenReturn(new User(1L, "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(String.valueOf(resourceId), date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, 1L, "Resource Name", "Workspace"));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Collections.singletonList(existingBooking));

        BookingConflictException exception = assertThrows(BookingConflictException.class, () -> bookingService.addBooking());

        assertEquals("Booking conflict: The resource is already booked during this time.", exception.getMessage());
    }

    /**
     * Test case for cancelling a booking successfully.
     */
    @Test
    public void testCancelBookingSuccessfully() {
        Long bookingId = 1L;
        User currentUser = new User(1L, "user-name", "password", "USER");
        Booking booking = new Booking(bookingId, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(inputReader.readLine()).thenReturn(String.valueOf(bookingId));
        when(bookingRepository.getBookingById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking();

        verify(bookingRepository).deleteBooking(bookingId);
    }

    /**
     * Test case for cancelling a booking that does not exist.
     */
    @Test
    public void testCancelBookingNotFound() {
        Long bookingId = 1L;

        when(userService.getCurrentUser()).thenReturn(new User(1L, "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(String.valueOf(bookingId));
        when(bookingRepository.getBookingById(bookingId)).thenReturn(Optional.empty());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outContent));

        BookingNotFoundException thrownException = assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking());

        assertEquals(YELLOW + "Booking with ID " + bookingId + " not found" + RESET, thrownException.getMessage());

        String expectedOutput = CYAN + "Enter booking ID: " + RESET;
        assertEquals(expectedOutput, outContent.toString());

        System.setOut(originalSystemOut);

        verify(bookingRepository, never()).deleteBooking(anyLong());
    }
    /**
     * Test case for viewing bookings of the current user.
     *
     * @throws ResourceNotFoundException if resource is not found
     * @throws UserNotFoundException if user is not found
     */
    @Test
    public void testViewUserBookings() throws ResourceNotFoundException, UserNotFoundException {
        User currentUser = new User(1L, "user-name", "password", "USER");
        Resource resource = new Resource(2L, 1L, "Resource Name", "Workspace");
        List<Booking> userBookings = List.of(
                new Booking(3L, 1L, resource.getId(), LocalDateTime.now(), LocalDateTime.now().plusHours(1))
        );

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId(currentUser.getId())).thenReturn(userBookings);
        when(userService.getUserById(currentUser.getId())).thenReturn(currentUser);
        when(resourceService.getResourceById(resource.getId())).thenReturn(resource);

        bookingService.viewUserBookings();

        verify(bookingRepository).getBookingsByUserId(currentUser.getId());
    }
    /**
     * Test case for viewing available booking slots for a resource.
     */
    @Test
    public void testViewAvailableSlots() {
        Long resourceId = 2L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startOfDay = LocalTime.of(9, 0);
        List<Booking> bookings = List.of(
                new Booking(3L, 1L, resourceId, LocalDateTime.of(date, startOfDay.plusHours(1)), LocalDateTime.of(date, startOfDay.plusHours(2))),
                new Booking(4L, 2L, resourceId, LocalDateTime.of(date, startOfDay.plusHours(4)), LocalDateTime.of(date, startOfDay.plusHours(5)))
        );

        when(inputReader.readLine()).thenReturn(String.valueOf(resourceId), date.toString());
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(bookings);

        bookingService.viewAvailableSlots();

        verify(bookingRepository).getBookingsByResourceId(resourceId);
    }

    /**
     * Test case for updating a booking successfully without conflicts.
     */
    @Test
    public void testUpdateBookingSuccessfully() {
        Long bookingId = 5L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime newStartTime = LocalTime.of(12, 0);
        LocalTime newEndTime = LocalTime.of(13, 0);
        Booking existingBooking = new Booking(bookingId, 1L, 2L, LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));

        when(userService.getCurrentUser()).thenReturn(new User(1L, "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(String.valueOf(bookingId), date.toString(), newStartTime.toString(), newEndTime.toString());
        when(bookingRepository.getBookingById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.getBookingsByResourceId(existingBooking.getResourceId())).thenReturn(Collections.singletonList(existingBooking));

        bookingService.updateBooking();

        verify(bookingRepository).updateBooking(any(Booking.class));
    }

    /**
     * Test case for updating a booking that conflicts with another booking.
     */
    @Test
    public void testUpdateBookingConflict() {
        Long bookingId = 6L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime newStartTime = LocalTime.of(10, 0);
        LocalTime newEndTime = LocalTime.of(11, 0);
        Booking existingBooking = new Booking(bookingId, 1L, 2L, LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        Booking conflictingBooking = new Booking(7L, 2L, 2L, LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(12, 0)));

        when(userService.getCurrentUser()).thenReturn(new User(1L, "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(String.valueOf(bookingId), date.toString(), newStartTime.toString(), newEndTime.toString());
        when(bookingRepository.getBookingById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.getBookingsByResourceId(existingBooking.getResourceId())).thenReturn(List.of(existingBooking, conflictingBooking));

        BookingConflictException exception = assertThrows(BookingConflictException.class, () -> bookingService.updateBooking());

        assertEquals("Booking conflict: The resource is already booked during this time.", exception.getMessage());
    }

    /**
     * Test case for updating a booking that does not exist.
     */
    @Test
    public void testUpdateBookingNotFound() {
        Long bookingId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime newStartTime = LocalTime.of(12, 0);
        LocalTime newEndTime = LocalTime.of(11, 0);

        when(userService.getCurrentUser()).thenReturn(new User(1L, "user-name", "password", "USER"));
        when(inputReader.readLine()).thenReturn(String.valueOf(bookingId), date.toString(), newStartTime.toString(), newEndTime.toString());

        assertThrows(BookingNotFoundException.class, () -> bookingService.updateBooking());
    }

    /**
     * Test case for getting booking date time successfully when adding a new booking.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testGetBookingDateTime_SuccessfulBooking() throws ResourceNotFoundException {
        Long resourceId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(inputReader.readLine()).thenReturn(date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, 1L, "Resource Name", "Workspace"));
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
        Long resourceId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 23);

        // Моделируем поведение inputReader для предоставления тестовых данных
        when(inputReader.readLine()).thenReturn(date.toString(), "invalid-time", "11:00", "12:00");

        // Каптурируем вывод в System.out
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Выполняем метод, который проверяется
        LocalDateTime[] dateTimes = bookingService.getBookingDateTime(resourceId, false);

        // Проверяем, что вернулись ожидаемые значения LocalDateTime
        LocalDateTime expectedStartDateTime = LocalDateTime.of(date, LocalTime.of(11, 0));
        LocalDateTime expectedEndDateTime = LocalDateTime.of(date, LocalTime.of(12, 0));
        assertArrayEquals(new LocalDateTime[]{expectedStartDateTime, expectedEndDateTime}, dateTimes);

        // Проверяем, что ошибка формата времени была выведена в консоль
        String expectedOutput = CYAN + "Enter start time (HH:MM): " + RESET +
                RED + "Invalid time format. Please use HH:MM format." + RESET + System.lineSeparator() +
                CYAN + "Enter start time (HH:MM): " + RESET;

        assertTrue(outContent.toString().contains(expectedOutput));

        // Восстанавливаем System.out
        System.setOut(originalSystemOut);
    }

    /**
     * Test case for getting booking date time when viewing available slots.
     *
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testGetBookingDateTime_ViewAvailableSlots() throws ResourceNotFoundException {
        Long resourceId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(inputReader.readLine()).thenReturn(date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, 1L, "Resource Name", "Workspace"));
        when(bookingRepository.getBookingsByResourceId(resourceId)).thenReturn(Collections.emptyList());

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
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Test
    public void testManageBookings_AddBooking() throws ResourceNotFoundException {
        User currentUser = new User(1L, "username", "password", "USER");
        Long resourceId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 23);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(inputReader.getUserChoice()).thenReturn(1, 0);
        when(inputReader.readLine()).thenReturn(String.valueOf(resourceId), date.toString(), startTime.toString(), endTime.toString());
        when(resourceService.getResourceById(resourceId)).thenReturn(new Resource(resourceId, 1L, "name", "type"));

        bookingService.manageBookings();

        verify(inputReader, times(2)).getUserChoice();
        verify(inputReader, times(4)).readLine();
        verify(resourceService).getResourceById(resourceId);
    }

    /**
     * Test case for managing bookings: cancelling a booking.
     */
    @Test
    public void testManageBookings_CancelBooking() {
        User currentUser = new User(1L, "username", "password", "USER");
        Long bookingId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 20, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 20, 11, 0);
        Booking booking = new Booking(bookingId, 1L, 1L, startTime, endTime);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.getBookingById(bookingId)).thenReturn(Optional.of(booking));
        when(inputReader.getUserChoice()).thenReturn(2, 0);
        when(inputReader.readLine()).thenReturn(String.valueOf(bookingId));

        bookingService.manageBookings();

        verify(inputReader, times(2)).getUserChoice();
        verify(inputReader, times(1)).readLine();
        verify(bookingRepository).deleteBooking(bookingId);
    }

    /**
     * Test case for managing bookings: viewing user's bookings.
     *
     * @throws ResourceNotFoundException if resource is not found
     * @throws UserNotFoundException if user is not found
     */
    @Test
    public void testManageBookings_ViewUserBookings() throws UserNotFoundException, ResourceNotFoundException {
        User currentUser = new User(1L, "username", "password", "USER");
        Booking booking1 = new Booking(1L, 1L, 1L, LocalDateTime.of(2023, 6, 23, 10, 0), LocalDateTime.of(2023, 6, 23, 11, 0));
        Booking booking2 = new Booking(2L, 1L, 2L, LocalDateTime.of(2023, 6, 24, 12, 0), LocalDateTime.of(2023, 6, 24, 13, 0));
        List<Booking> userBookings = Arrays.asList(booking1, booking2);
        Resource resource = new Resource(1L, 1L, "name", "type");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(bookingRepository.getBookingsByUserId(currentUser.getId())).thenReturn(userBookings);
        when(userService.getUserById(currentUser.getId())).thenReturn(currentUser);
        when(resourceService.getResourceById(anyLong())).thenReturn(resource);
        when(inputReader.readLine()).thenReturn("3", "0");

        bookingService.manageBookings();

    }

    /**
     * Test case for managing bookings: updating a booking.
     */
    @Test
    public void testManageBookings_UpdateBooking() {
        User currentUser = new User(1L, "username", "password", "USER");
        Long bookingId = 1L;
        Booking existingBooking = new Booking(bookingId, 1L, 1L, LocalDateTime.of(2023, 6, 23, 9, 0), LocalDateTime.of(2023, 6, 23, 10, 0));
        List<Booking> userBookings = List.of(
                new Booking(2L, 1L, 1L, LocalDateTime.of(2023, 6, 23, 10, 0), LocalDateTime.of(2023, 6, 23, 11, 0)),
                new Booking(3L, 1L, 2L, LocalDateTime.of(2023, 6, 24, 12, 0), LocalDateTime.of(2023, 6, 24, 13, 0))
        );

        when(bookingRepository.getBookingById(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.getBookingsByResourceId(existingBooking.getResourceId())).thenReturn(userBookings);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(inputReader.getUserChoice()).thenReturn(4, 0);
        when(inputReader.readLine()).thenReturn(String.valueOf(bookingId), "2023-06-23", "09:00", "10:00");

        bookingService.manageBookings();

        verify(inputReader, times(2)).getUserChoice();
        verify(inputReader, times(4)).readLine();
        verify(bookingRepository).updateBooking(any(Booking.class));
    }

    /**
     * Test case for managing bookings: viewing available booking slots.
     */
    @Test
    public void testManageBookings_ViewAvailableSlots() {
        User currentUser = new User(1L, "username", "password", "USER");
        Long resourceId = 1L;
        LocalDate date = LocalDate.of(2023, 6, 20);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(inputReader.getUserChoice()).thenReturn(5,0);
        when(inputReader.readLine()).thenReturn(String.valueOf(resourceId), date.toString());

        bookingService.manageBookings();

        verify(inputReader, times(2)).getUserChoice();
        verify(inputReader, times(2)).readLine();
    }

    /**
     * Test case for filtering bookings by date.
     *
     * @throws ResourceNotFoundException if resource is not found
     * @throws UserNotFoundException if user is not found
     */
    @Test
    public void testFilterBookings_ByDate_Success() throws UserNotFoundException, ResourceNotFoundException {
        // Подготовка данных для теста
        User currentUser = new User(1L, "username", "password", "USER");
        LocalDate date = LocalDate.of(2023, 6, 23);
        Resource resource = new Resource(1L, 1L, "Resource Name", "Workspace");
        Booking booking1 = new Booking(1L, 1L, 1L, LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        Booking booking2 = new Booking(2L, 1L, 1L, LocalDateTime.of(date, LocalTime.of(12, 0)), LocalDateTime.of(date, LocalTime.of(13, 0)));
        List<Booking> bookingsByDate = Arrays.asList(booking1, booking2);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getUserById(currentUser.getId())).thenReturn(currentUser);
        when(resourceService.getResourceById(anyLong())).thenReturn(resource);
        when(inputReader.getUserChoice()).thenReturn(1);  // Выбор действия для фильтрации
        when(inputReader.readLine()).thenReturn(date.toString());  // Ввод даты
        when(bookingRepository.getAllBookings()).thenReturn(bookingsByDate);  // Возвращаем список бронирований

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outputStream));

        bookingService.filterBookings();

        System.setOut(originalSystemOut);
    }
}
