package ru.y_lab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.y_lab.annotation.Loggable;
import ru.y_lab.dto.*;
import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.mapper.CustomBookingMapper;
import ru.y_lab.mapper.CustomDateTimeMapper;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.repo.ResourceRepository;
import ru.y_lab.repo.UserRepository;
import ru.y_lab.service.BookingService;
import ru.y_lab.util.AuthenticationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private final AuthenticationUtil authUtil;
    private final CustomBookingMapper bookingMapper;
    private final CustomDateTimeMapper dateTimeMapper;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDTO addBooking(AddBookingRequestDTO requestDTO, UserDTO currentUser) {
        Long resourceId = requestDTO.resourceId();
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by resource name: " + resourceId));

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

    @Override
    public BookingWithOwnerResourceDTO getBookingById(Long bookingId) throws BookingNotFoundException, UserNotFoundException, ResourceNotFoundException {
        Booking booking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found by ID: " + bookingId));
        Resource resource = resourceRepository.getResourceById(booking.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + booking.getResourceId()));
        User user = userRepository.getUserById(booking.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + booking.getUserId()));

        return bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user);
    }

    @Override
    public List<BookingWithOwnerResourceDTO> getUserBookings(Long userId) throws BookingNotFoundException, UserNotFoundException, ResourceNotFoundException {
        List<Booking> allBookings = bookingRepository.getBookingsByUserId(userId)
                .orElseThrow(() -> new BookingNotFoundException("User " + userId + " have not any booking"));

        return convertListBookingsToDTO(allBookings);
    }

    @Override
    public List<BookingWithOwnerResourceDTO> getAllBookings() throws BookingNotFoundException, UserNotFoundException, ResourceNotFoundException {
        List<Booking> allBookings = bookingRepository.getAllBookings()
                .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking"));

        return convertListBookingsToDTO(allBookings);
    }

    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByDate(Long date) throws BookingNotFoundException, UserNotFoundException, ResourceNotFoundException {
        LocalDate localDate = dateTimeMapper.toLocalDate(date);
        List<Booking> allBookingsByDate = bookingRepository.getBookingsByDate(localDate)
                .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking by date: " + localDate));

        return convertListBookingsToDTO(allBookingsByDate);
    }

    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByUserId(Long userId) throws BookingNotFoundException, UserNotFoundException, ResourceNotFoundException {
        List<Booking> allBookingsByUserId = bookingRepository.getBookingsByUserId(userId)
                .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking by user ID: " + userId));

        return convertListBookingsToDTO(allBookingsByUserId);
    }

    @Override
    public List<BookingWithOwnerResourceDTO> getBookingsByResourceId(Long resourceId) throws BookingNotFoundException, UserNotFoundException, ResourceNotFoundException {
        List<Booking> allBookingsByUserId = bookingRepository.getBookingsByResourceId(resourceId)
                .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking by user ID: " + resourceId));

        return convertListBookingsToDTO(allBookingsByUserId);
    }

    @Override
    public List<AvailableSlotDTO> getAvailableSlots(AvailableSlotsRequestDTO request) throws BookingNotFoundException {
        Long resourceId = request.resourceId();
        LocalDate date = dateTimeMapper.toLocalDate(request.date());

        List<Booking> bookings = bookingRepository.getBookingsByResourceId(resourceId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found by resource ID: " + resourceId));
        List<Booking> filteredBookings = filterBookingsByDate(bookings, date);

        return calculateAvailableSlots(filteredBookings);
    }

    @Override
    public BookingDTO updateBooking(Long bookingId, UpdateBookingRequestDTO request, UserDTO currentUser) throws BookingNotFoundException, BookingConflictException {
        validateUpdateBookingRequest(request);

        Booking existingBooking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found"));

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

    @Override
    public void deleteBooking(Long bookingId, UserDTO currentUser) {
        Booking booking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found by ID: " + bookingId));
        authUtil.authorize(currentUser, booking.getUserId());

        bookingRepository.deleteBooking(bookingId);
    }

    private void checkBookingConflicts(Booking booking) throws BookingConflictException {
        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(booking.getResourceId())
                .orElseThrow(() -> new BookingConflictException("Booking not found by resource ID: " + booking.getResourceId()))
                .stream()
                .filter(existingBooking -> !existingBooking.getId().equals(booking.getId()))
                .toList();

        for (Booking existingBooking : existingBookings) {
            if (existingBooking.getStartTime().isBefore(booking.getEndTime()) &&
                    booking.getStartTime().isBefore(existingBooking.getEndTime())) {
                throw new BookingConflictException("Booking conflict: The resource is already booked during this time.");
            }
        }
    }

    private List<Booking> filterBookingsByDate(List<Booking> bookings, LocalDate date) {
        return bookings.stream()
                .filter(booking -> booking.getStartTime().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Booking::getStartTime))
                .toList();
    }

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
            availableSlots.add(new AvailableSlotDTO(i++,
                    formatLocalTime(slotStart),
                    formatLocalTime(endOfDay)));
        }

        return availableSlots;
    }

    private List<BookingWithOwnerResourceDTO> convertListBookingsToDTO(List<Booking> bookings) throws UserNotFoundException, ResourceNotFoundException {
        List<BookingWithOwnerResourceDTO> bookingWithOwnerResourceDTOList = new ArrayList<>();
        for (Booking booking : bookings) {
            User user = userRepository.getUserById(booking.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + booking.getUserId()));
            Resource resource = resourceRepository.getResourceById(booking.getResourceId())
                    .orElseThrow(() -> new UserNotFoundException("Resource not found by ID: " + booking.getResourceId()));
            BookingWithOwnerResourceDTO bookingWithOwnerResourceDTO = bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user);
            bookingWithOwnerResourceDTOList.add(bookingWithOwnerResourceDTO);
        }
        return bookingWithOwnerResourceDTOList;
    }
}
