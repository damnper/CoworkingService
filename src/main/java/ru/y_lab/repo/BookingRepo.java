package ru.y_lab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.y_lab.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Booking entities.
 * This interface extends JpaRepository and provides methods
 * to interact with the database using SQL queries.
 */
@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    /**
     * Adds a new booking to the repository.
     *
     * @param userId the ID of the user associated with the booking
     * @param resourceId the ID of the resource being booked
     * @param startTime the start time of the booking
     * @param endTime the end time of the booking
     * @return the new Booking
     */
    @Modifying
    @Query(value = """
            INSERT INTO coworking_service.bookings (user_id, resource_id, start_time, end_time)
            VALUES (:userId, :resourceId, :startTime, :endTime)
            """, nativeQuery = true)
    Booking addBooking(@Param("userId") Long userId,
                   @Param("resourceId") Long resourceId,
                   @Param("startTime") LocalDate startTime,
                   @Param("endTime") LocalDate endTime);

    /**
     * Retrieves bookings by user ID.
     *
     * @param userId the ID of the user
     * @return a list of bookings for the specified user
     */
    @Query(value = """
            SELECT * FROM coworking_service.bookings
            WHERE user_id = :userId
            """, nativeQuery = true)
    List<Booking> findByUserId(@Param("userId") Long userId);

    /**
     * Retrieves bookings by resource ID.
     *
     * @param resourceId the ID of the resource
     * @return a list of bookings for the specified resource
     */
    @Query(value = """
            SELECT * FROM coworking_service.bookings
            WHERE resource_id = :resourceId
            """, nativeQuery = true)
    List<Booking> findByResourceId(@Param("resourceId") Long resourceId);

    /**
     * Retrieves bookings by date.
     *
     * @param date the date of the booking
     * @return a list of bookings for the specified date
     */
    @Query(value = """
            SELECT * FROM coworking_service.bookings
            WHERE start_time::date = :date
            """, nativeQuery = true)
    List<Booking> findByDate(@Param("date") LocalDate date);

    /**
     * Retrieves all bookings from the repository.
     *
     * @return a list of all bookings
     */
    @Query(value = "SELECT * FROM coworking_service.bookings", nativeQuery = true)
    List<Booking> findAllBookings();

    /**
     * Updates an existing booking in the repository.
     *
     * @param id the ID of the booking to update
     * @param userId the new user ID associated with the booking
     * @param resourceId the new resource ID associated with the booking
     * @param startTime the new start time of the booking
     * @param endTime the new end time of the booking
     * @return optional Booking
     */
    @Modifying
    @Query(value = """
            UPDATE coworking_service.bookings
            SET user_id = :userId, resource_id = :resourceId, start_time = :startTime, end_time = :endTime
            WHERE id = :id
            """, nativeQuery = true)
    Optional<Booking> updateBooking(@Param("id") Long id,
                      @Param("userId") Long userId,
                      @Param("resourceId") Long resourceId,
                      @Param("startTime") LocalDate startTime,
                      @Param("endTime") LocalDate endTime);

    /**
     * Deletes a booking from the repository by its ID.
     *
     * @param id the ID of the booking to be deleted
     */
    @Modifying
    @Query(value = "DELETE FROM coworking_service.bookings WHERE id = :id", nativeQuery = true)
    void deleteBooking(@Param("id") Long id);
}
