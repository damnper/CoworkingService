package ru.y_lab.repo;


import ru.y_lab.config.DatabaseManager;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The BookingRepository class provides methods to manage bookings.
 * It stores booking data in a HashMap and supports CRUD operations.
 */
public class BookingRepository {

    /**
     * Adds a new booking to the repository.
     * @param booking the booking to be added
     */
    public void addBooking(Booking booking) {
        String sql = "INSERT INTO coworking_service.bookings (id, user_id, resource_id, start_time, end_time) VALUES (DEFAULT, (?), (?), (?), (?))";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, booking.getUserId());
            ps.setLong(2, booking.getResourceId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a booking by its ID.
     * @param id the ID of the booking
     * @return the booking with the specified ID, or throws BookingNotFoundException if not found
     * @throws BookingNotFoundException if the booking with the specified ID is not found
     */
    public Optional<Booking> getBookingById(Long id) {
        String sql = "SELECT * FROM coworking_service.bookings WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return Optional.ofNullable(rs.next() ? mapRowToBooking(rs) : null);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving booking by ID: " + id, e);
        }
    }

    /**
     * Updates an existing booking in the repository.
     * @param booking the booking with updated information
     * @throws BookingNotFoundException if the booking with the specified ID is not found
     */
    public void updateBooking(Booking booking) {
        String sql = "UPDATE coworking_service.bookings " +
                     "SET user_id = ?, resource_id = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, booking.getUserId());
            ps.setLong(2, booking.getResourceId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            ps.setLong(5, booking.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new BookingNotFoundException("Booking with ID " + booking.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating booking", e);
        }
    }

    /**
     * Deletes a booking from the repository by its ID.
     * @param id the ID of the booking to be deleted
     * @throws BookingNotFoundException if the booking with the specified ID is not found
     */
    public void deleteBooking(Long id) {
        String sql = "DELETE FROM coworking_service.bookings WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new BookingNotFoundException("Booking with ID " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting booking", e);
        }
    }

    /**
     * Retrieves all bookings from the repository.
     * @return a list of all bookings
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM coworking_service.bookings";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Booking booking = mapRowToBooking(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving all bookings", e);
        }
        return bookings;
    }

    /**
     * Retrieves bookings by user ID.
     * @param userId the ID of the user
     * @return a list of bookings for the specified user
     */
    public List<Booking> getBookingsByUserId(Long userId) {
        List<Booking> result = new ArrayList<>();
        String sql = "SELECT * FROM coworking_service.bookings WHERE user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    result.add(booking);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving booking by ID", e);
        }
        return result;
    }

    /**
     * Retrieves bookings by resource ID.
     * @param resourceId the ID of the resource
     * @return a list of bookings for the specified resource
     */
    public List<Booking> getBookingsByResourceId(Long resourceId) {
        List<Booking> result = new ArrayList<>();
        String sql = "SELECT * FROM coworking_service.bookings WHERE bookings.resource_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, resourceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    result.add(booking);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving booking by ID", e);
        }
        return result;
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

