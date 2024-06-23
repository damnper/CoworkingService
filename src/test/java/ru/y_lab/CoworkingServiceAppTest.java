package ru.y_lab;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.y_lab.CoworkingServiceApp.showAdminMenu;
import static ru.y_lab.CoworkingServiceApp.showMainMenu;

/**
 * Unit tests for {@link CoworkingServiceApp}.
 */
public class CoworkingServiceAppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private UserService userService;

    /**
     * Sets up the test environment by mocking {@link UserService} and redirecting System.out and System.err
     * to capture console output.
     */
    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }


    /**
     * Restores System.out and System.err after each test to their original streams.
     */
    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Test case for the main method of {@link CoworkingServiceApp}.
     * Verifies the output of the main menu when executed with simulated user input.
     *
     * @throws BookingConflictException if there is a booking conflict
     * @throws ResourceNotFoundException if a resource is not found
     * @throws UserNotFoundException if a user is not found
     */
    @Test
    public void testMainMethod() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        when(userService.getCurrentUser()).thenReturn(null);

        String simulatedInput = "1\nusername\npassword\n2\nusername\npassword\n0\n";
        InputStream savedSystemIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            CoworkingServiceApp.main(new String[]{});
        } finally {
            System.setIn(savedSystemIn);
        }

        String[] lines = outContent.toString().split(System.lineSeparator());
        assertEquals("--- Coworking Service Main Menu ---", lines[0].trim());
        assertEquals("Logged in as: Guest", lines[1].trim());
        assertEquals("1. Register", lines[2].trim());
        assertEquals("2. Login", lines[3].trim());
        assertEquals("0. Exit", lines[6].trim());
    }

    /**
     * Test case for {@link CoworkingServiceApp#showMainMenu()}.
     * Verifies the output of the main menu displayed to the user.
     */
    @Test
    public void testShowMainMenu() {
        // Mocking current user
        when(userService.getCurrentUser()).thenReturn(null);

        // Call the method under test
        showMainMenu();

        // Assert output as per the expected format
        String[] lines = outContent.toString().split(System.lineSeparator());
        assertEquals("--- Coworking Service Main Menu ---", lines[0].trim());
        assertEquals("1. Register", lines[2].trim());
        assertEquals("2. Login", lines[3].trim());
        assertEquals("3. Manage Resources", lines[4].trim());
        assertEquals("4. Manage Bookings", lines[5].trim());
        assertEquals("0. Exit", lines[6].trim());
        assertEquals("Enter your choice:", lines[7].trim());
    }

    /**
     * Test case for {@link CoworkingServiceApp#showAdminMenu()}.
     * Verifies the output of the admin menu displayed to the user.
     */
    @Test
    public void testShowAdminMenu() {
        // Mocking current user
        when(userService.getCurrentUser()).thenReturn(null);

        // Call the method under test
        showAdminMenu();

        // Assert output as per the expected format
        String[] lines = outContent.toString().split(System.lineSeparator());
        assertEquals("--- Coworking Service Admin Menu ---", lines[0].trim());
        assertEquals("1. Register", lines[2].trim());
        assertEquals("2. Login", lines[3].trim());
        assertEquals("3. View All Users", lines[4].trim());
        assertEquals("4. Manage Resources", lines[5].trim());
        assertEquals("5. Manage Bookings", lines[6].trim());
        assertEquals("6. Filter Bookings", lines[7].trim());
        assertEquals("0. Exit", lines[8].trim());
        assertEquals("Enter your choice:", lines[9].trim());
    }

}
