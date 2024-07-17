package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.*;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.BookingMapper;
import ru.y_lab.mapper.CustomDateTimeMapper;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.service.BookingService;
import ru.y_lab.util.AuthenticationUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static ru.y_lab.enums.RoleType.ADMIN;
import static ru.y_lab.enums.RoleType.USER;
import static ru.y_lab.mapper.CustomDateTimeMapper.formatLocalTime;
import static ru.y_lab.util.ValidationUtil.*;

/**
 * The BookingServiceImpl class provides an implementation of the BookingService interface.
 * It interacts with the BookingRepository to perform CRUD operations.
 */
@Loggable
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final AuthenticationUtil authUtil;
    private final BookingMapper bookingMapper;
    private final CustomDateTimeMapper dateTimeMapper;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final BookingRepository bookingRepository;

    /**
     * Adds a new booking to the system.
     *
     * @param requestDTO the request containing booking details
     * @param httpRequest the HTTP request to get the session
     * @return the added booking as a BookingDTO
     */
    @Override
    public BookingDTO addBooking(AddBookingRequestDTO requestDTO, HttpServletRequest httpRequest) {
        validateAddBookingRequest(requestDTO);
        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        Long resourceId = requestDTO.resourceId();
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("The requested resource was not found."));

        LocalDateTime startDateTime = dateTimeMapper.toLocalDateTime(requestDTO.startTime());
        LocalDateTime endDateTime = dateTimeMapper.toLocalDateTime(requestDTO.endTime());

        validateDateTime(startDateTime, endDateTime);
        Booking booking = Booking.builder()
                .userId(currentUser.id())
                .resourceId(resource.getId())
                .startTime(startDateTime)
                .endTime(endDateTime)
                .build();
        checkBookingConflicts(booking);
        Booking savedBooking = bookingRepository.addBooking(booking);
        return bookingMapper.toDTO(savedBooking);
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId the ID of the booking
     * @param httpRequest the HTTP request to get the session
     * @return the booking with owner and resource details as a BookingWithOwnerResourceDTO
     */
    @Override
    public BookingWithOwnerResourceDTO getBookingById(Long bookingId, HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, USER.name());

        Booking booking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The requested booking was not found."));
        Resource resource = resourceRepository.getResourceById(booking.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("The resource for the booking was not found."));
        User user = userRepository.getUserById(booking.getUserId())
                .orElseThrow(() -> new UserNotFoundException("The user who made the booking was not found."));

        return bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user);
    }

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified user
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getUserBookings(Long userId, HttpServletRequest httpRequest) {
        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        authUtil.authorize(currentUser, userId);

        List<Booking> allBookings = bookingRepository.getBookingsByUserId(userId)
                .orElseThrow(() -> new BookingNotFoundException("No bookings were found for the user."));

        return convertListBookingsToDTO(allBookings);
    }

    /**
     * Retrieves all bookings in the system. Only accessible by admin users.
     *
     * @param httpRequest the HTTP request to get the session
     * @return a list of all bookings with owner and resource details
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getAllBookings(HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, ADMIN.name());
        List<Booking> allBookings = bookingRepository.getAllBookings()
                .orElseThrow(() -> new BookingNotFoundException("No bookings were found in the system."));

        return convertListBookingsToDTO(allBookings);
    }

    /**
     * Retrieves bookings by a specific date.
     *
     * @param date the date of the bookings
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified date
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByDate(Long date, HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, USER.name());

        LocalDate localDate = dateTimeMapper.toLocalDate(date);
        List<Booking> allBookingsByDate = bookingRepository.getBookingsByDate(localDate)
                .orElseThrow(() -> new BookingNotFoundException("No bookings were found for the specified date."));

        return convertListBookingsToDTO(allBookingsByDate);
    }

    /**
     * Retrieves bookings made by a specific user ID. Only accessible by admin users.
     *
     * @param userId the ID of the user
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified user
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByUserId(Long userId, HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, ADMIN.name());

        List<Booking> allBookingsByUserId = bookingRepository.getBookingsByUserId(userId)
                .orElseThrow(() -> new BookingNotFoundException("The user who made the booking was not found or no bookings were found for the specified user."));

        return convertListBookingsToDTO(allBookingsByUserId);
    }

    /**
     * Retrieves bookings for a specific resource ID.
     *
     * @param resourceId the ID of the resource
     * @param httpRequest the HTTP request to get the session
     * @return a list of bookings with owner and resource details for the specified resource
     */
    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByResourceId(Long resourceId, HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, USER.name());

        List<Booking> allBookingsByUserId = bookingRepository.getBookingsByResourceId(resourceId)
                .orElseThrow(() -> new BookingNotFoundException("The resource for the booking was not found or no bookings were found for the specified resource."));

        return convertListBookingsToDTO(allBookingsByUserId);
    }

    /**
     * Retrieves available slots for a specific resource and date.
     *
     * @param request the request containing resource ID and date
     * @param httpRequest the HTTP request to get the session
     * @return a list of available slots for the specified resource and date
     */
    @Override
    public List<AvailableSlotDTO> getAvailableSlots(AvailableSlotsRequestDTO request, HttpServletRequest httpRequest) {
        authUtil.authenticate(httpRequest, USER.name());

        Long resourceId = request.resourceId();
        LocalDate date = dateTimeMapper.toLocalDate(request.date());

        List<Booking> bookings = bookingRepository.getBookingsByResourceId(resourceId)
                .orElseThrow(() -> new BookingNotFoundException("The resource for the booking was not found or no bookings were found for the specified resource."));
        List<Booking> filteredBookings = filterBookingsByDate(bookings, date);

        return calculateAvailableSlots(filteredBookings);
    }

    /**
     * Updates an existing booking.
     *
     * @param bookingId the ID of the booking to be updated
     * @param request the update request containing updated booking details
     * @param httpRequest the HTTP request to get the session
     * @return the updated booking as a BookingDTO
     */
    @Override
    public BookingDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request, HttpServletRequest httpRequest) {
        validateUpdateBookingRequest(request);

        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        Booking existingBooking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The booking to be updated was not found."));
        authUtil.authorize(currentUser, existingBooking.getUserId());

        LocalDateTime startDateTime = dateTimeMapper.toLocalDateTime(request.startTime());
        LocalDateTime endDateTime = dateTimeMapper.toLocalDateTime(request.endTime());
        validateDateTime(startDateTime, endDateTime);

        existingBooking.setStartTime(startDateTime);
        existingBooking.setEndTime(endDateTime);
        checkBookingConflicts(existingBooking);

        Booking updatedBooking = bookingRepository.updateBooking(existingBooking);
        return bookingMapper.toDTO(updatedBooking);
    }

    /**
     * Deletes a booking by its ID.
     *
     * @param bookingId the ID of the booking to be deleted
     * @param httpRequest the HTTP request to get the session
     */
    @Override
    public void deleteBooking(Long bookingId, HttpServletRequest httpRequest) {
        UserDTO currentUser = authUtil.authenticate(httpRequest, USER.name());
        Booking booking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("The booking to be deleted was not found."));
        authUtil.authorize(currentUser, booking.getUserId());

        bookingRepository.deleteBooking(bookingId);
    }

    /**
     * Checks for booking conflicts with existing bookings for a given resource.
     * If a conflict is found, a BookingConflictException is thrown.
     *
     * @param booking the booking to check for conflicts
     * @throws BookingConflictException if the resource is already booked during the specified time period
     */
    private void checkBookingConflicts(Booking booking) {
        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(booking.getResourceId())
                .orElseGet(Collections::emptyList)
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
     * Converts a list of Booking objects to a list of BookingWithOwnerResourceDTO objects.
     *
     * @param bookings the list of bookings to convert
     * @return a list of BookingWithOwnerResourceDTO objects
     * @throws UserNotFoundException if a user for a booking is not found
     * @throws ResourceNotFoundException if a resource for a booking is not found
     */
    private List<BookingWithOwnerResourceDTO> convertListBookingsToDTO(List<Booking> bookings) throws UserNotFoundException, ResourceNotFoundException {
        List<BookingWithOwnerResourceDTO> bookingWithOwnerResourceDTOList = new ArrayList<>();
        bookings.forEach(booking -> {
            User user = userRepository.getUserById(booking.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + booking.getUserId()));
            Resource resource = resourceRepository.getResourceById(booking.getResourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + booking.getResourceId()));
            BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user);
            bookingWithOwnerResourceDTOList.add(bookingWithOwnerResourceDTO);
        });
        return bookingWithOwnerResourceDTOList;
    }
}
