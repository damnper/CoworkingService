//package ru.y_lab.repo;
//
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.*;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import ru.y_lab.config.DatabaseConfig;
//import ru.y_lab.config.DatabaseManager;
//import ru.y_lab.exception.BookingNotFoundException;
//import ru.y_lab.model.Booking;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Unit tests for the BookingRepository class.
// */
//@Testcontainers
//@DisplayName("Unit Tests for BookingRepositoryTest")
//public class BookingRepositoryTest {
//
//    private BookingRepository bookingRepository;
//
//    @Container
//    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test");
//
//    @BeforeAll
//    public static void setUpBeforeAll() {
//        postgresContainer.start();
//        DatabaseConfig dbConfig = new DatabaseConfig();
//        dbConfig.setDriverClassName("org.postgresql.Driver");
//        dbConfig.setUrl(postgresContainer.getJdbcUrl());
//        dbConfig.setUsername(postgresContainer.getUsername());
//        dbConfig.setPassword(postgresContainer.getPassword());
//        DatabaseManager.setConfig(dbConfig);
//
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute("CREATE SCHEMA IF NOT EXISTS coworking_service");
//
//                stmt.execute("SET search_path TO coworking_service");
//
//                stmt.execute("CREATE TABLE IF NOT EXISTS coworking_service.bookings (" +
//                        "id BIGSERIAL PRIMARY KEY, " +
//                        "user_id BIGINT NOT NULL, " +
//                        "resource_id BIGINT NOT NULL, " +
//                        "start_time TIMESTAMP NOT NULL, " +
//                        "end_time TIMESTAMP NOT NULL" +
//                        ");");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error setting up test database", e);
//        }
//    }
//
//    @AfterAll
//    public static void tearDownAfterAll() {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute("DROP SCHEMA IF EXISTS coworking_service CASCADE");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error tearing down test database", e);
//        }
//    }
//
//    @BeforeEach
//    public void setUp() {
//        bookingRepository = new BookingRepository();
//    }
//
//    @AfterEach
//    public void tearDown() {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute("TRUNCATE TABLE coworking_service.bookings RESTART IDENTITY CASCADE");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error tearing down the database after test", e);
//        }
//    }
//
//    /**
//     * Test case for adding a booking to the repository.
//     */
//    @Test
//    @DisplayName("Test adding a new booking")
//    public void testAddBooking() {
//        LocalDateTime startTime = LocalDateTime.of(2024, 6, 29, 11, 18, 10, 307171000);
//        LocalDateTime endTime = startTime.plusHours(1);
//        Booking booking = new Booking(null, 1L, 1L, startTime, endTime);
//
//        bookingRepository.saveBooking(booking);
//
//        Booking retrievedBooking = bookingRepository.getBookingById(booking.getId()).orElse(null);
//        assertNotNull(retrievedBooking);
//
//        assertEquals(booking.getId(), retrievedBooking.getId());
//        assertEquals(booking.getUserId(), retrievedBooking.getUserId());
//        assertEquals(booking.getResourceId(), retrievedBooking.getResourceId());
//        assertEquals(booking.getStartTime(), retrievedBooking.getStartTime());
//        assertEquals(booking.getEndTime(), retrievedBooking.getEndTime());
//    }
//
//    /**
//     * Test case for retrieving a booking by ID.
//     *
//     * @throws BookingNotFoundException if the booking is not found
//     */
//    @Test
//    @DisplayName("Test for retrieving a booking by ID")
//    @SneakyThrows
//    public void testGetBookingById() {
//        LocalDateTime startTime = LocalDateTime.of(2024, 6, 29, 11, 18, 10, 307171000);
//        LocalDateTime endTime = startTime.plusHours(1);
//        Booking booking = new Booking(null, 1L, 1L, startTime, endTime);
//
//        bookingRepository.saveBooking(booking);
//
//        Booking retrievedBooking = bookingRepository.getBookingById(booking.getId()).orElse(null);
//        assertNotNull(retrievedBooking);
//        assertEquals(booking, retrievedBooking);
//    }
//
//    /**
//     * Test case for retrieving a non-existent booking by ID, expecting an exception.
//     */
//    @Test
//    @DisplayName("Test for retrieving a non-existent booking by ID")
//    public void testGetBookingByIdNotFound() {
//        Optional<Booking> optionalBooking = bookingRepository.getBookingById(999L);
//
//        assertTrue(optionalBooking.isEmpty(), "Booking should have not been found");
//    }
//
//    /**
//     * Test case for updating an existing booking.
//     *
//     * @throws BookingNotFoundException if the booking to update is not found
//     */
//    @Test
//    @DisplayName("Test for updating an existing booking")
//    @SneakyThrows
//    public void testUpdateBooking() {
//        LocalDateTime fixedStartTime = LocalDateTime.of(2024, 6, 29, 11, 18, 10, 307171000);
//        LocalDateTime fixedEndTime = fixedStartTime.plusHours(1);
//
//        Booking booking = new Booking(null, 1L, 1L, fixedStartTime, fixedEndTime);
//
//        Booking savedBooking = bookingRepository.saveBooking(booking);
//
//        LocalDateTime newStartTime = LocalDateTime.of(2024, 6, 30, 12, 0, 0, 0);
//        LocalDateTime newEndTime = newStartTime.plusHours(2);
//
//        Booking updatedBooking = new Booking(savedBooking.getId(), 1L, 1L, newStartTime, newEndTime);
//
//        bookingRepository.updateBooking(updatedBooking);
//
//        Booking retrievedBooking = bookingRepository.getBookingById(savedBooking.getId()).orElse(null);
//
//        assertNotNull(retrievedBooking, "The updated booking should not be null.");
//
//        // Проверяем, что обновленное бронирование имеет правильные значения
//        assertEquals(updatedBooking.getId(), retrievedBooking.getId(), "Booking ID does not match.");
//        assertEquals(updatedBooking.getUserId(), retrievedBooking.getUserId(), "User ID does not match.");
//        assertEquals(updatedBooking.getResourceId(), retrievedBooking.getResourceId(), "Resource ID does not match.");
//        assertEquals(updatedBooking.getStartTime(), retrievedBooking.getStartTime(), "Start time does not match.");
//        assertEquals(updatedBooking.getEndTime(), retrievedBooking.getEndTime(), "End time does not match.");
//
//    }
//
//    /**
//     * Test case for updating a non-existent booking, expecting an exception.
//     */
//    @Test
//    @DisplayName("Test for updating a non-existent booking")
//    public void testUpdateNonExistentBooking() {
//        Booking nonExistentBooking = new Booking(999L, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
//        assertThrows(RuntimeException.class, () -> bookingRepository.updateBooking(nonExistentBooking));
//    }
//
//    /**
//     * Test case for deleting an existing booking.
//     */
//    @Test
//    @DisplayName("Test for deleting an existing booking")
//    public void testDeleteBooking() {
//        Booking booking = new Booking(null, 1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
//        bookingRepository.saveBooking(booking);
//
//        bookingRepository.deleteBooking(booking.getId());
//
//        Optional<Booking> optionalResource = bookingRepository.getBookingById(booking.getId());
//        assertTrue(optionalResource.isEmpty(), "Booking should have been deleted");
//    }
//
//    /**
//     * Test case for deleting a non-existent booking, expecting an exception.
//     */
//    @Test
//    @DisplayName("Test for deleting a non-existent booking")
//    public void testDeleteNonExistentBooking() {
//        assertThrows(RuntimeException.class, () -> bookingRepository.deleteBooking(999L));
//    }
//
//    /**
//     * Test case for retrieving all bookings from the repository.
//     */
//    @Test
//    @DisplayName("Test for retrieving all bookings from the repository")
//    public void testGetAllBookings() {
//        LocalDateTime fixedStartTime = LocalDateTime.of(2024, 6, 29, 11, 18, 10, 307171000);
//        LocalDateTime fixedEndTime = fixedStartTime.plusHours(1);
//
//        Booking booking1 = new Booking(null, 1L, 1L, fixedStartTime, fixedEndTime);
//        Booking booking2 = new Booking(null, 2L, 2L, fixedStartTime, fixedEndTime);
//
//        Booking savedBooking1 = bookingRepository.saveBooking(booking1);
//        Booking savedBooking2 = bookingRepository.saveBooking(booking2);
//
//        Optional<List<Booking>> allBookingsOpt = bookingRepository.getAllBookings();
//
//        assertTrue(allBookingsOpt.isPresent(), "The list of bookings should not be empty.");
//
//        List<Booking> allBookings = allBookingsOpt.get();
//
//        assertEquals(2, allBookings.size(), "The number of bookings retrieved does not match the expected size.");
//
//        assertTrue(allBookings.stream().anyMatch(b ->
//                b.getId().equals(savedBooking1.getId()) &&
//                        b.getUserId().equals(booking1.getUserId()) &&
//                        b.getResourceId().equals(booking1.getResourceId()) &&
//                        b.getStartTime().equals(booking1.getStartTime()) &&
//                        b.getEndTime().equals(booking1.getEndTime())
//        ), "Booking1 is not present in the retrieved bookings.");
//
//        assertTrue(allBookings.stream().anyMatch(b ->
//                b.getId().equals(savedBooking2.getId()) &&
//                        b.getUserId().equals(booking2.getUserId()) &&
//                        b.getResourceId().equals(booking2.getResourceId()) &&
//                        b.getStartTime().equals(booking2.getStartTime()) &&
//                        b.getEndTime().equals(booking2.getEndTime())
//        ), "Booking2 is not present in the retrieved bookings.");
//    }
//
//    /**
//     * Test case for retrieving bookings by user ID.
//     */
//    @Test
//    @DisplayName("Test for retrieving bookings by user ID")
//    public void testGetBookingsByUserId() {
//        LocalDateTime fixedStartTime = LocalDateTime.of(2024, 6, 29, 11, 18, 10, 307171000);
//        LocalDateTime fixedEndTime = fixedStartTime.plusHours(1);
//
//        Booking booking1 = new Booking(null, 1L, 1L, fixedStartTime, fixedEndTime);
//        Booking booking2 = new Booking(null, 1L, 2L, fixedStartTime, fixedEndTime);
//
//        Booking savedBooking1 = bookingRepository.saveBooking(booking1);
//        Booking savedBooking2 = bookingRepository.saveBooking(booking2);
//
//        Optional<List<Booking>> bookingsByUserIdOpt = bookingRepository.getBookingsByUserId(1L);
//
//        assertTrue(bookingsByUserIdOpt.isPresent(), "The list of bookings should not be empty.");
//
//        List<Booking> bookingsByUserId = bookingsByUserIdOpt.get();
//
//        assertEquals(2, bookingsByUserId.size(), "The number of bookings retrieved does not match the expected size.");
//
//        assertAll("Verify all retrieved bookings for user ID 1",
//                () -> assertTrue(bookingsByUserId.stream().anyMatch(b ->
//                        b.getId().equals(savedBooking1.getId()) &&
//                                b.getUserId().equals(booking1.getUserId()) &&
//                                b.getResourceId().equals(booking1.getResourceId()) &&
//                                b.getStartTime().equals(booking1.getStartTime()) &&
//                                b.getEndTime().equals(booking1.getEndTime())
//                ), "Booking1 is not present in the retrieved bookings."),
//                () -> assertTrue(bookingsByUserId.stream().anyMatch(b ->
//                        b.getId().equals(savedBooking2.getId()) &&
//                                b.getUserId().equals(booking2.getUserId()) &&
//                                b.getResourceId().equals(booking2.getResourceId()) &&
//                                b.getStartTime().equals(booking2.getStartTime()) &&
//                                b.getEndTime().equals(booking2.getEndTime())
//                ), "Booking2 is not present in the retrieved bookings.")
//        );
//    }
//
//
//    /**
//     * Test case for retrieving bookings by resource ID.
//     */
//    @Test
//    @DisplayName("Test for retrieving bookings by resource ID")
//    public void testGetBookingsByResourceId() {
//        LocalDateTime fixedStartTime = LocalDateTime.of(2024, 6, 29, 11, 18, 10, 307171000);
//        LocalDateTime fixedEndTime = fixedStartTime.plusHours(1);
//
//        Booking booking1 = new Booking(null, 1L, 1L, fixedStartTime, fixedEndTime);
//        Booking booking2 = new Booking(null, 2L, 1L, fixedStartTime, fixedEndTime);
//
//        Booking savedBooking1 = bookingRepository.saveBooking(booking1);
//        Booking savedBooking2 = bookingRepository.saveBooking(booking2);
//
//        Optional<List<Booking>> bookingsByResourceIdOpt = bookingRepository.getBookingsByResourceId(1L);
//
//        assertTrue(bookingsByResourceIdOpt.isPresent(), "The list of bookings should not be empty.");
//
//        List<Booking> bookingsByResourceId = bookingsByResourceIdOpt.get();
//
//        assertEquals(2, bookingsByResourceId.size(), "The number of bookings retrieved does not match the expected size.");
//
//        assertAll("Verify all retrieved bookings for resource ID 1",
//                () -> assertTrue(bookingsByResourceId.stream().anyMatch(b ->
//                        b.getId().equals(savedBooking1.getId()) &&
//                                b.getUserId().equals(booking1.getUserId()) &&
//                                b.getResourceId().equals(booking1.getResourceId()) &&
//                                b.getStartTime().equals(booking1.getStartTime()) &&
//                                b.getEndTime().equals(booking1.getEndTime())
//                ), "Booking1 is not present in the retrieved bookings."),
//                () -> assertTrue(bookingsByResourceId.stream().anyMatch(b ->
//                        b.getId().equals(savedBooking2.getId()) &&
//                                b.getUserId().equals(booking2.getUserId()) &&
//                                b.getResourceId().equals(booking2.getResourceId()) &&
//                                b.getStartTime().equals(booking2.getStartTime()) &&
//                                b.getEndTime().equals(booking2.getEndTime())
//                ), "Booking2 is not present in the retrieved bookings.")
//        );
//    }
//}