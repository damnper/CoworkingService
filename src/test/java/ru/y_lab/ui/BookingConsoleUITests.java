package ru.y_lab.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.ui.impl.BookingConsoleUI;
import ru.y_lab.util.InputReader;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class BookingConsoleUITests {

    @Mock
    private UserService userService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private InputReader inputReader;

    @InjectMocks
    private BookingConsoleUI bookingConsoleUI;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowBookingMenu() {
        when(userService.getCurrentUser()).thenReturn(new User(1L, "testUser", "password", "USER"));

        bookingConsoleUI.showBookingMenu(userService);

        verify(userService, times(1)).getCurrentUser();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testShowFilterMenu() {
        when(userService.getCurrentUser()).thenReturn(new User(1L, "testUser", "password", "USER"));

        bookingConsoleUI.showFilterMenu(userService, inputReader);

        verify(userService, times(1)).getCurrentUser();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testPrintBookings_noBookings() throws UserNotFoundException, ResourceNotFoundException {
        bookingConsoleUI.printBookings(Collections.emptyList(), userService, resourceService);

        // Проверка вывода на консоль
        // Например, используйте ByteArrayOutputStream для захвата и проверки вывода
    }

    @Test
    public void testPrintBookings_withBookings() throws UserNotFoundException, ResourceNotFoundException {
        User user = new User(1L, "testUser", "password", "USER");
        when(userService.getUserById(anyLong())).thenReturn(user);
        Resource resource = new Resource(1L, 1L, "Test Resource", "Type A");
        when(resourceService.getResourceById(anyLong())).thenReturn(resource);

        Booking booking = new Booking(1L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        List<Booking> bookings = List.of(booking);

        bookingConsoleUI.printBookings(bookings, userService, resourceService);
    }

    @Test
    public void testShowUserBooking() throws UserNotFoundException, ResourceNotFoundException {
        User user = new User(1L, "testUser", "password", "USER");
        when(userService.getUserById(anyLong())).thenReturn(user);
        Resource resource = new Resource(1L, 1L, "Test Resource", "Type A");
        when(resourceService.getResourceById(anyLong())).thenReturn(resource);

        Booking booking = new Booking(1L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        List<Booking> bookings = List.of(booking);

        bookingConsoleUI.showUserBooking(bookings, userService, resourceService);
    }
}
