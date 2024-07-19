package ru.y_lab.repo;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.y_lab.config.DatabaseManager;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.DatabaseException;
import ru.y_lab.model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The BookingRepository class provides methods to manage bookings.
 * It interacts with the database to perform CRUD operations for bookings.
 */
@Repository
@AllArgsConstructor
public class BookingRepository {

    private DatabaseManager databaseManager;

    // SQL Queries as constants
    private static final String ADD_BOOKING_SQL = "INSERT INTO coworking_service.bookings (id, user_id, resource_id, start_time, end_time) VALUES (DEFAULT, ?, ?, ?, ?)";
    private static final String GET_BOOKING_BY_ID_SQL = "SELECT * FROM coworking_service.bookings WHERE id = ?";
    private static final String GET_BOOKINGS_BY_USER_ID_SQL = "SELECT * FROM coworking_service.bookings WHERE user_id = ?";
    private static final String GET_BOOKINGS_BY_RESOURCE_ID_SQL = "SELECT * FROM coworking_service.bookings WHERE resource_id = ?";
    private static final String GET_BOOKINGS_BY_DATE_SQL = "SELECT * FROM coworking_service.bookings WHERE start_time::date = ?";
    private static final String GET_ALL_BOOKINGS_SQL = "SELECT * FROM coworking_service.bookings";
    private static final String UPDATE_BOOKING_SQL = "UPDATE coworking_service.bookings SET user_id = ?, resource_id = ?, start_time = ?, end_time = ? WHERE id = ?";
    private static final String DELETE_BOOKING_SQL = "DELETE FROM coworking_service.bookings WHERE id = ?";

    /**
     * Adds a new booking to the repository.
     * @param booking the booking to be added
     * @return the new booking
     */
    public Booking addBooking(Booking booking) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(ADD_BOOKING_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, booking.getUserId());
            ps.setLong(2, booking.getResourceId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Unable to create booking. Please try again.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Booking created but unable to retrieve booking ID. Please try again.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while adding the booking. Please try again later.");
        }
        return booking;
    }

    /**
     * Retrieves a booking by its ID.
     * @param id the ID of the booking
     * @return the booking with the specified ID
     */
    public Optional<Booking> getBookingById(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_BOOKING_BY_ID_SQL)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    return Optional.of(booking);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving the booking by ID. Please try again later.");
        }
    }

    /**
     * Retrieves bookings by user ID.
     * @param userId the ID of the user
     * @return a list of bookings for the specified user
     */
    public Optional<List<Booking>> getBookingsByUserId(Long userId) {
        return getBookings(GET_BOOKINGS_BY_USER_ID_SQL, userId);
    }

    /**
     * Retrieves bookings by resource ID.
     * @param resourceId the ID of the resource
     * @return a list of bookings for the specified resource
     */
    public Optional<List<Booking>> getBookingsByResourceId(Long resourceId) {
        return getBookings(GET_BOOKINGS_BY_RESOURCE_ID_SQL, resourceId);
    }

    /**
     * Retrieves bookings by date.
     * @param date the date of the booking
     * @return a list of bookings for the specified date
     */
    public Optional<List<Booking>> getBookingsByDate(LocalDate date) {
        List<Booking> result = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_BOOKINGS_BY_DATE_SQL)) {
            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    result.add(booking);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving bookings by date. Please try again later.");
        }
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    /**
     * Retrieves all bookings from the repository.
     * @return a list of all bookings
     */
    public Optional<List<Booking>> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(GET_ALL_BOOKINGS_SQL)) {

            while (rs.next()) {
                Booking booking = mapRowToBooking(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving all bookings. Please try again later.");
        }
        return bookings.isEmpty() ? Optional.empty() : Optional.of(bookings);
    }

    /**
     * Updates an existing booking in the repository.
     * @param booking the booking with updated information
     * @return the updated booking
     */
    public Booking updateBooking(Booking booking) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement updatePs = connection.prepareStatement(UPDATE_BOOKING_SQL);
             PreparedStatement selectPs = connection.prepareStatement(GET_BOOKING_BY_ID_SQL)) {

            // Check if booking exists
            selectPs.setLong(1, booking.getId());
            try (ResultSet rs = selectPs.executeQuery()) {
                if (!rs.next()) {
                    throw new BookingNotFoundException("Booking with ID " + booking.getId() + " not found.");
                }
            }

            updatePs.setLong(1, booking.getUserId());
            updatePs.setLong(2, booking.getResourceId());
            updatePs.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            updatePs.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            updatePs.setLong(5, booking.getId());

            updatePs.executeUpdate();

            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBooking(rs);
                } else {
                    throw new BookingNotFoundException("Booking with ID " + booking.getId() + " not found after update.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while updating the booking. Please try again later.");
        }
    }

    /**
     * Deletes a booking from the repository by its ID.
     * @param id the ID of the booking to be deleted
     * @throws BookingNotFoundException if the booking with the specified ID is not found
     */
    public void deleteBooking(Long id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_BOOKING_SQL)) {

            ps.setLong(1, id);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new BookingNotFoundException("Booking with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while deleting the booking. Please try again later.");
        }
    }

    private Optional<List<Booking>> getBookings(String sql, Long id) {
        List<Booking> result = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    result.add(booking);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("An error occurred while retrieving bookings. Please try again later.");
        }
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    /**
     * Maps a row from a ResultSet to a Booking object.
     * @param rs the ResultSet containing the data to map
     * @return a Booking object mapped from the ResultSet data
     * @throws SQLException if there is an error accessing the ResultSet
     */
    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        return Booking.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .resourceId(rs.getLong("resource_id"))
                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                .build();
    }
}