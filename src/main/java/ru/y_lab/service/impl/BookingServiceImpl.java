package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.AdminOnly;
import ru.y_lab.annotation.AdminOrOwner;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.*;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.mapper.BookingMapper;
import ru.y_lab.mapper.CustomDateTimeMapper;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.repo.BookingRepo;
import ru.y_lab.repo.ResourceRepo;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.JWTService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.y_lab.mapper.CustomDateTimeMapper.formatLocalTime;
import static ru.y_lab.util.ValidationUtil.validateDateTime;
import static ru.y_lab.util.ValidationUtil.validateUpdateBookingRequest;

/**
 * The BookingServiceImpl class provides an implementation of the BookingService interface.
 * It interacts with the BookingRepository to perform CRUD operations.
 */
@Loggable
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final CustomDateTimeMapper dateTimeMapper;
    private final ResourceRepo resourceRepo;
    private final BookingRepo bookingRepo;
    private final JWTService jwtService;

    /**
     * Adds a new booking to the system.
     *
     * @param token      the authentication token of the user making the request
     * @param requestDTO the request containing booking details
     * @return the added booking as a BookingDTO
     */
    @Override
    public BookingDTO addBooking(String token, AddBookingRequestDTO requestDTO) {
        Long userId = jwtService.extractUserId(token);
        Resource resource = resourceRepo.findById(requestDTO.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("The requested resource was not found."));

        Booking booking = createBooking(userId, resource, requestDTO);
        Booking savedBooking = bookingRepo.save(booking);
        return bookingMapper.toDTO(savedBooking);
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param token     the authentication token of the user making the request
     * @param bookingId the ID of the booking
     * @return the booking with owner and resource details as a BookingWithOwnerResourceDTO
     */
    @Override
    @AdminOnly
    public BookingWithOwnerResourceDTO getBookingById(String token, Long bookingId) {
        return bookingRepo.findBookingWithOwnerResourceById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The requested booking was not found."));
    }

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param token the authentication token of the user making the request
     * @return a list of bookings with owner and resource details for the specified user
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getUserBookings(String token) {
        Long userId = jwtService.extractUserId(token);
        List<BookingWithOwnerResourceDTO> bookings = bookingRepo.findBookingWithOwnerResourceByUserId(userId);
        if (bookings.isEmpty())
            throw new BookingNotFoundException("No bookings were found in the system.");
        return bookings;
    }

    /**
     * Retrieves all bookings in the system. Only accessible by admin users.
     *
     * @param token the authentication token of the admin user making the request
     * @return a list of all bookings with owner and resource details
     */
    @Override
    @AdminOnly
    public List<BookingWithOwnerResourceDTO> getAllBookings(String token) {
        List<BookingWithOwnerResourceDTO> bookings = bookingRepo.findAllBookingWithOwnerResource();
        if (bookings.isEmpty())
            throw new BookingNotFoundException("No bookings were found in the system.");
        return bookings;
    }

    /**
     * Retrieves bookings by a specific date.
     *
     * @param token the authentication token of the user making the request
     * @param date  the date of the bookings in milliseconds since epoch
     * @return a list of bookings with owner and resource details for the specified date
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByDate(String token, Long date) {
        LocalDate localDate = dateTimeMapper.toLocalDate(date);
        String dateString = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<BookingWithOwnerResourceDTO> bookings = bookingRepo.findBookingWithOwnerResourceByDate(dateString);
        if (bookings.isEmpty())
            throw new BookingNotFoundException("No bookings were found in the system.");
        return bookings;
    }

    /**
     * Retrieves bookings made by a specific user ID. Only accessible by admin users.
     *
     * @param userId the ID of the user
     * @return a list of bookings with owner and resource details for the specified user
     */
    @Override
    @AdminOnly
    public List<BookingWithOwnerResourceDTO> getBookingsByUserId(Long userId) {
        List<BookingWithOwnerResourceDTO> bookings = bookingRepo.findBookingWithOwnerResourceByUserId(userId);
        if (bookings.isEmpty())
            throw new BookingNotFoundException("No bookings were found in the system.");
        return bookings;
    }

    /**
     * Retrieves bookings for a specific resource ID.
     *
     * @param token      the authentication token of the user making the request
     * @param resourceId the ID of the resource
     * @return a list of bookings with owner and resource details for the specified resource
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByResourceId(String token, Long resourceId) {
        List<BookingWithOwnerResourceDTO> bookings = bookingRepo.findBookingWithOwnerResourceByResourceId(resourceId);
        if (bookings.isEmpty())
            throw new BookingNotFoundException("No bookings were found in the system.");
        return bookings;
    }

    /**
     * Retrieves available slots for a specific resource and date.
     *
     * @param token   the authentication token of the user making the request
     * @param request the request containing resource ID and date
     * @return a list of available slots for the specified resource and date
     */
    @Override
    public List<AvailableSlotDTO> getAvailableSlots(String token, AvailableSlotsRequestDTO request) {

        Long resourceId = request.resourceId();
        LocalDate date = dateTimeMapper.toLocalDate(request.date());

        List<Booking> bookings = bookingRepo.findByResourceId(resourceId);
        if(bookings.isEmpty())
            throw new BookingNotFoundException("The resource for the booking was not found or no bookings were found for the specified resource.");
        List<Booking> filteredBookings = filterBookingsByDate(bookings, date);

        return calculateAvailableSlots(filteredBookings);
    }

    /**
     * Updates an existing booking.
     *
     * @param token     the authentication token of the user making the request
     * @param bookingId the ID of the booking to be updated
     * @param request   the update request containing updated booking details
     * @return the updated booking as a BookingDTO
     */
    @Override
    @AdminOrOwner
    public BookingDTO updateBooking(String token, Long bookingId, UpdateBookingRequestDTO request) {
        validateUpdateBookingRequest(request);

        Booking existingBooking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The booking to be updated was not found."));

        processBookingTimes(request, existingBooking);
        checkBookingConflicts(existingBooking);

        Booking updatedBooking = bookingRepo.save(existingBooking);
        return bookingMapper.toDTO(updatedBooking);
    }


    /**
     * Deletes a booking by its ID.
     *
     * @param token     the authentication token of the user making the request
     * @param bookingId the ID of the booking to be deleted
     */
    @Override
    @AdminOrOwner
    public void deleteBooking(String token, Long bookingId) {
        bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The booking to be deleted was not found."));

        bookingRepo.deleteById(bookingId);
    }

    /**
     * Creates a new booking.
     *
     * @param userId    the ID of the user making the booking
     * @param resource  the resource being booked
     * @param requestDTO the request containing booking details
     * @return the created booking
     */
    private Booking createBooking(Long userId, Resource resource, AddBookingRequestDTO requestDTO) {
        LocalDateTime startDateTime = dateTimeMapper.toLocalDateTime(requestDTO.startTime());
        LocalDateTime endDateTime = dateTimeMapper.toLocalDateTime(requestDTO.endTime());
        validateDateTime(startDateTime, endDateTime);

        Booking booking = Booking.builder()
                .userId(userId)
                .resourceId(resource.getId())
                .startTime(startDateTime)
                .endTime(endDateTime)
                .build();
        checkBookingConflicts(booking);
        return booking;
    }

    /**
     * Checks for booking conflicts with existing bookings for a given resource.
     * If a conflict is found, a BookingConflictException is thrown.
     *
     * @param booking the booking to check for conflicts
     * @throws BookingConflictException if the resource is already booked during the specified time period
     */
    private void checkBookingConflicts(Booking booking) {
        List<Booking> existingBookings = bookingRepo.findByResourceId(booking.getResourceId())
                .stream()
                .filter(existingBooking -> !existingBooking.getId().equals(booking.getId()))
                .toList();

        for (Booking existingBooking : existingBookings) {
            if (existingBooking.getStartTime().isBefore(booking.getEndTime()) &&
                    booking.getStartTime().isBefore(existingBooking.getEndTime())) {
                throw new BookingConflictException("The resource is already booked for the specified time period.");
            }
        }
    }

    /**
     * Filters bookings to include only those that occur on a specific date.
     *
     * @param bookings the list of bookings to filter
     * @param date the date to filter bookings by
     * @return a list of bookings that occur on the specified date, sorted by start time
     */
    private List<Booking> filterBookingsByDate(List<Booking> bookings, LocalDate date) {
        return bookings.stream()
                .filter(booking -> booking.getStartTime().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Booking::getStartTime))
                .toList();
    }

    /**
     * Calculates available slots based on existing bookings.
     *
     * @param bookings the list of existing bookings
     * @return a list of available slots as AvailableSlotDTO objects
     */
    private List<AvailableSlotDTO> calculateAvailableSlots(List<Booking> bookings) {
        List<AvailableSlotDTO> availableSlots = new ArrayList<>();
        LocalTime startOfDay = LocalTime.of(9, 0);
        LocalTime endOfDay = LocalTime.of(18, 0);
        LocalTime slotStart = startOfDay;
        int i = 1;

        for (Booking booking : bookings) {
            LocalTime bookingStart = booking.getStartTime().toLocalTime();
            LocalTime bookingEnd = booking.getEndTime().toLocalTime();

            if (slotStart.isBefore(bookingStart)) {
                availableSlots.add(new AvailableSlotDTO(i++,
                        formatLocalTime(slotStart),
                        formatLocalTime(bookingStart)));
            }
            slotStart = bookingEnd;
        }

        if (slotStart.isBefore(endOfDay)) {
            availableSlots.add(new AvailableSlotDTO(i,
                    formatLocalTime(slotStart),
                    formatLocalTime(endOfDay)));
        }

        return availableSlots;
    }

    /**
     * Processes the booking times from the request and updates the existing booking.
     *
     * @param request the update request containing updated booking details
     * @param existingBooking the existing booking to be updated
     */
    private void processBookingTimes(UpdateBookingRequestDTO request, Booking existingBooking) {
        LocalDateTime startDateTime = dateTimeMapper.toLocalDateTime(request.startTime());
        LocalDateTime endDateTime = dateTimeMapper.toLocalDateTime(request.endTime());
        validateDateTime(startDateTime, endDateTime);

        existingBooking.setStartTime(startDateTime);
        existingBooking.setEndTime(endDateTime);
    }
}
