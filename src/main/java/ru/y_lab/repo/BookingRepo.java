package ru.y_lab.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.y_lab.dto.BookingWithOwnerResourceDTO;
import ru.y_lab.model.Booking;

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
     * Retrieves a booking along with its owner and resource details by booking ID.
     *
     * @param bookingId the ID of the booking
     * @return an optional containing the booking with owner and resource details
     */
    @Query(value = """
            SELECT u.id as ownerId,
                   r.id as resourceId,
                   b.id as bookingId,
                   u.username as ownerName,
                   r.name as resourceName,
                   r.type as resourceType,
                   TO_CHAR(b.start_time, 'YYYY-MM-DD') as date,
                   TO_CHAR(b.start_time, 'HH24:MI') as startTime,
                   TO_CHAR(b.end_time, 'HH24:MI') as endTime
            FROM coworking_service.bookings b
            JOIN coworking_service.resources r
                ON b.resource_id = r.id
            JOIN coworking_service.users u
                ON b.user_id = u.id
            WHERE b.id = :bookingId""", nativeQuery = true)
    Optional<BookingWithOwnerResourceDTO> findBookingWithOwnerResourceById(@Param("bookingId") Long bookingId);

    /**
     * Retrieves bookings along with their owner and resource details by user ID.
     *
     * @param userId the ID of the user
     * @return a list of bookings with owner and resource details for the specified user
     */
    @Query(value = """
            SELECT u.id as ownerId,
                   r.id as resourceId,
                   b.id as bookingId,
                   u.username as ownerName,
                   r.name as resourceName,
                   r.type as resourceType,
                   TO_CHAR(b.start_time, 'YYYY-MM-DD') as date,
                   TO_CHAR(b.start_time, 'HH24:MI') as startTime,
                   TO_CHAR(b.end_time, 'HH24:MI') as endTime
            FROM coworking_service.bookings b
            JOIN coworking_service.resources r
                ON b.resource_id = r.id
            JOIN coworking_service.users u
                ON b.user_id = u.id
            WHERE b.id = :userId""", nativeQuery = true)
    List<BookingWithOwnerResourceDTO> findBookingWithOwnerResourceByUserId(@Param("userId") Long userId);

    /**
     * Retrieves bookings along with their owner and resource details by resource ID.
     *
     * @param resourceId the ID of the resource
     * @return a list of bookings with owner and resource details for the specified resource
     */
    @Query(value = """
            SELECT u.id as ownerId,
                   r.id as resourceId,
                   b.id as bookingId,
                   u.username as ownerName,
                   r.name as resourceName,
                   r.type as resourceType,
                   TO_CHAR(b.start_time, 'YYYY-MM-DD') as date,
                   TO_CHAR(b.start_time, 'HH24:MI') as startTime,
                   TO_CHAR(b.end_time, 'HH24:MI') as endTime
            FROM coworking_service.bookings b
            JOIN coworking_service.resources r
                ON b.resource_id = r.id
            JOIN coworking_service.users u
                ON b.user_id = u.id
            WHERE b.id = :resourceId""", nativeQuery = true)
    List<BookingWithOwnerResourceDTO> findBookingWithOwnerResourceByResourceId(@Param("resourceId") Long resourceId);

    /**
     * Retrieves all bookings along with their owner and resource details.
     *
     * @return a list of all bookings with owner and resource details
     */
    @Query(value = """
            SELECT u.id as ownerId,
                   r.id as resourceId,
                   b.id as bookingId,
                   u.username as ownerName,
                   r.name as resourceName,
                   r.type as resourceType,
                   TO_CHAR(b.start_time, 'YYYY-MM-DD') as date,
                   TO_CHAR(b.start_time, 'HH24:MI') as startTime,
                   TO_CHAR(b.end_time, 'HH24:MI') as endTime
            FROM coworking_service.bookings b
            JOIN coworking_service.resources r
                ON b.resource_id = r.id
            JOIN coworking_service.users u
                ON b.user_id = u.id""", nativeQuery = true)
    List<BookingWithOwnerResourceDTO> findAllBookingWithOwnerResource();

    /**
     * Retrieves bookings along with their owner and resource details by date.
     *
     * @param date the date of the bookings (in 'YYYY-MM-DD' format)
     * @return a list of bookings with owner and resource details for the specified date
     */
    @Query(value = """
            SELECT u.id as ownerId,
                   r.id as resourceId,
                   b.id as bookingId,
                   u.username as ownerName,
                   r.name as resourceName,
                   r.type as resourceType,
                   TO_CHAR(b.start_time, 'YYYY-MM-DD') as date,
                   TO_CHAR(b.start_time, 'HH24:MI') as startTime,
                   TO_CHAR(b.end_time, 'HH24:MI') as endTime
            FROM coworking_service.bookings b
            JOIN coworking_service.resources r
                ON b.resource_id = r.id
            JOIN coworking_service.users u
                ON b.user_id = u.id
            WHERE TO_CHAR(b.start_time, 'YYYY-MM-DD') = :date""", nativeQuery = true)
    List<BookingWithOwnerResourceDTO> findBookingWithOwnerResourceByDate(@Param("date") String date);

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
}
