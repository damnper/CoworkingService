package ru.y_lab.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.*;
import static ru.y_lab.mapper.CustomDateTimeMapper.formatLocalTime;
import static ru.y_lab.util.RequestUtil.getRequestBody;
import static ru.y_lab.util.RequestUtil.parseRequest;
import static ru.y_lab.util.ResponseUtil.sendErrorResponse;
import static ru.y_lab.util.ResponseUtil.sendSuccessResponse;
import static ru.y_lab.util.ValidationUtil.*;

/**
 * The BookingServiceImpl class provides an implementation of the BookingService interface.
 * It interacts with the BookingRepository to perform CRUD operations.
 */
@Loggable
public class BookingServiceImpl implements BookingService {

    private final AuthenticationUtil authUtil;
    private final CustomBookingMapper bookingMapper;
    private final CustomDateTimeMapper dateTimeMapper;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl() {
        authUtil = new AuthenticationUtil();
         bookingMapper = new CustomBookingMapper();
         dateTimeMapper = new CustomDateTimeMapper();
         userRepository = new UserRepository();
         resourceRepository = new ResourceRepository();
         bookingRepository = new BookingRepository();
    }

    public BookingServiceImpl(AuthenticationUtil authUtil, CustomBookingMapper bookingMapper, CustomDateTimeMapper dateTimeMapper, UserRepository userRepository, ResourceRepository resourceRepository, BookingRepository bookingRepository) {
        this.authUtil = authUtil;
        this.bookingMapper = bookingMapper;
        this.dateTimeMapper = dateTimeMapper;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Adds a new booking based on user input.
     * Prompts the user to enter the resource ID and booking times.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     */
    @Override
    public void addBooking(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            String requestBody = getRequestBody(req);
            AddBookingRequestDTO requestDTO = parseRequest(requestBody, AddBookingRequestDTO.class);
            validateAddBookingRequest(requestDTO);
            BookingDTO bookingDTO = processAddBooking(requestDTO, currentUser);
            sendSuccessResponse(resp, 201, bookingDTO);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        } catch (ResourceNotFoundException | BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (BookingConflictException e) {
            sendErrorResponse(resp, SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Retrieves a booking by its unique identifier.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getBookingById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, null);
            Long bookingId = Long.valueOf(req.getParameter("bookingId"));

            Booking booking = bookingRepository.getBookingById(bookingId)
                    .orElseThrow(() -> new BookingNotFoundException("Booking not found by ID: " + bookingId));
            Resource resource = resourceRepository.getResourceById(booking.getResourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found by ID: " + booking.getResourceId()));
            User user = userRepository.getUserById(booking.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found by ID: " + booking.getUserId()));

            BookingWithOwnerResourceDTO resourceWithOwnerDTO = bookingMapper.toBookingWithOwnerResourceDTO(booking, resource, user);
            sendSuccessResponse(resp, SC_OK, resourceWithOwnerDTO);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (UserNotFoundException | ResourceNotFoundException | BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves all bookings made by the currently authenticated user.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getUserBookings(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO userDTO = authUtil.authenticateAndAuthorize(req, null);
            List<Booking> allBookings = bookingRepository.getBookingsByUserId(userDTO.id())
                    .orElseThrow(() -> new BookingNotFoundException("User " + userDTO.id() + " have not any booking"));

            sendSuccessResponse(resp, 200, convertListBookingsToDTO(allBookings));
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (UserNotFoundException | ResourceNotFoundException | BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves all bookings in the system. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getAllBookings(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            authUtil.authenticateAndAuthorize(req, "ADMIN");
            List<Booking> allBookings = bookingRepository.getAllBookings()
                    .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking"));

            sendSuccessResponse(resp, 200, convertListBookingsToDTO(allBookings));
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (UserNotFoundException | ResourceNotFoundException | BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves bookings by a specific date. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getBookingsByDate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, "ADMIN");
            String dateParam = req.getParameter("date");

            LocalDate date = dateTimeMapper.toLocalDate(Long.valueOf(dateParam));
            List<Booking> allBookingsByDate = bookingRepository.getBookingsByDate(date)
                    .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking by date: " + date));

            sendSuccessResponse(resp, 200, convertListBookingsToDTO(allBookingsByDate));
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (UserNotFoundException | ResourceNotFoundException | BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Retrieves bookings by a specific user ID. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getBookingsByUserId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, "ADMIN");
            Long userId = Long.valueOf(req.getParameter("userId"));

            List<Booking> allBookingsByUserId = bookingRepository.getBookingsByUserId(userId)
                    .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking by user ID: " + userId));

            sendSuccessResponse(resp, 200, convertListBookingsToDTO(allBookingsByUserId));
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (UserNotFoundException | ResourceNotFoundException | BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Retrieves bookings by a specific resource ID. Only accessible by admin users.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getBookingsByResourceId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, "ADMIN");
            Long resourceId = Long.valueOf(req.getParameter("resourceId"));

            List<Booking> allBookingsByUserId = bookingRepository.getBookingsByResourceId(resourceId)
                    .orElseThrow(() -> new BookingNotFoundException("There are not any existing booking by user ID: " + resourceId));

            sendSuccessResponse(resp, 200, convertListBookingsToDTO(allBookingsByUserId));
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (UserNotFoundException | ResourceNotFoundException | BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Retrieves available slots for a specific resource and date.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void getAvailableSlots(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            authUtil.authenticateAndAuthorize(req, null);
            String requestBody = getRequestBody(req);
            AvailableSlotsRequestDTO availableSlotsRequestDTO = parseRequest(requestBody, AvailableSlotsRequestDTO.class);

            Long resourceId = availableSlotsRequestDTO.resourceId();
            LocalDate date = dateTimeMapper.toLocalDate(availableSlotsRequestDTO.date());

            List<Booking> bookings = bookingRepository.getBookingsByResourceId(resourceId)
                    .orElseThrow(() -> new BookingNotFoundException("Booking not found by resource ID: " + resourceId));
            List<Booking> filteredBookings = filterBookingsByDate(bookings, date);

            List<AvailableSlotDTO> availableSlots = calculateAvailableSlots(filteredBookings);
            sendSuccessResponse(resp, SC_OK, availableSlots);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Updates an existing booking based on user input.
     * Validates the user's access rights before updating the booking.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void updateBooking(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            String requestBody = getRequestBody(req);
            Long bookingIdToUpdate = Long.valueOf(req.getParameter("bookingId"));
            Booking booking = bookingRepository.getBookingById(bookingIdToUpdate)
                    .orElseThrow(() -> new BookingNotFoundException("Booking not found by ID: " + bookingIdToUpdate));

            if (!authUtil.isUserAuthorizedToAction(currentUser, booking.getUserId())) throw new SecurityException("Access denied");
            UpdateBookingRequestDTO requestDTO = parseRequest(requestBody, UpdateBookingRequestDTO.class);
            validateUpdateBookingRequest(requestDTO);
            BookingDTO bookingDTO = processUpdatingBooking(bookingIdToUpdate, requestDTO, booking);
            sendSuccessResponse(resp, 200, bookingDTO);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Cancels a booking based on user input.
     * Prompts the user to enter the booking ID and validates the user's access rights.
     *
     * @param req  the HttpServletRequest object containing the request details
     * @param resp the HttpServletResponse object for sending the response
     */
    @Override
    @SneakyThrows
    public void deleteBooking(HttpServletRequest req, HttpServletResponse resp) {
        try {
            UserDTO currentUser = authUtil.authenticateAndAuthorize(req, null);
            Long bookingIdToDelete = Long.valueOf(req.getParameter("bookingId"));
            Booking booking = bookingRepository.getBookingById(bookingIdToDelete).orElseThrow(() -> new BookingNotFoundException("Booking not found by ID: " + bookingIdToDelete));
            if (!authUtil.isUserAuthorizedToAction(currentUser, booking.getUserId())) throw new SecurityException("Access denied");

            processBookingDeletion(bookingIdToDelete);
            sendSuccessResponse(resp, 204);
        } catch (SecurityException e) {
            sendErrorResponse(resp, SC_UNAUTHORIZED, e.getMessage());
        } catch (BookingNotFoundException e) {
            sendErrorResponse(resp, SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(resp, SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Processes the addition of a new booking.
     *
     * @param addResourceRequest the AddBookingRequestDTO object containing booking details
     * @param currentUser the currently authenticated user
     * @return the created BookingDTO object
     * @throws ResourceNotFoundException if the resource is not found
     * @throws BookingConflictException if there is a booking conflict
     */
    private BookingDTO processAddBooking(AddBookingRequestDTO addResourceRequest, UserDTO currentUser) throws ResourceNotFoundException, BookingConflictException {
        Long resourceId = addResourceRequest.resourceId();
        Resource resource = resourceRepository.getResourceById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by resource name: " + resourceId));

        LocalDateTime startDateTime = dateTimeMapper.toLocalDateTime(addResourceRequest.startTime());
        LocalDateTime endDateTime = dateTimeMapper.toLocalDateTime(addResourceRequest.endTime());

        validateDateTime(startDateTime, endDateTime);
        Booking booking = Booking.builder()
                .userId(currentUser.id())
                .resourceId(resource.getId())
                .startTime(startDateTime)
                .endTime(endDateTime)
                .build();
        checkBookingConflicts(booking);
        Booking savedBooking = bookingRepository.saveBooking(booking);
        return bookingMapper.toDTO(savedBooking);
    }

    /**
     * Checks for booking conflicts with existing bookings.
     *
     * @param booking the Booking object to check for conflicts
     * @throws BookingConflictException if there is a booking conflict
     */
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

    /**
     * Filters bookings by a specific date.
     *
     * @param bookings the list of Booking objects to filter
     * @param date the date to filter bookings by
     * @return the list of filtered Booking objects
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
     * @param bookings the list of existing Booking objects
     * @return the list of AvailableSlotDTO objects representing available slots
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
            availableSlots.add(new AvailableSlotDTO(i++,
                    formatLocalTime(slotStart),
                    formatLocalTime(endOfDay)));
        }

        return availableSlots;
    }

    /**
     * Processes the updating of an existing booking.
     *
     * @param bookingIdToUpdate the ID of the booking to update
     * @param updateRequest the UpdateBookingRequestDTO object containing updated booking details
     * @param booking the existing Booking object to update
     * @return the updated BookingDTO object
     * @throws BookingConflictException if there is a booking conflict
     */
    private BookingDTO processUpdatingBooking(Long bookingIdToUpdate, UpdateBookingRequestDTO updateRequest, Booking booking) throws BookingConflictException {
        LocalDateTime startDateTime = dateTimeMapper.toLocalDateTime(updateRequest.startTime());
        LocalDateTime endDateTime = dateTimeMapper.toLocalDateTime(updateRequest.endTime());
        validateDateTime(startDateTime, endDateTime);

        try {
            booking.setStartTime(startDateTime);
            booking.setEndTime(endDateTime);
            checkBookingConflicts(booking);
            return bookingMapper.toDTO(bookingRepository.updateBooking(booking));
        } catch (BookingNotFoundException e) {
            throw new BookingNotFoundException("Booking not found by ID: " + bookingIdToUpdate);
        }
    }

    /**
     * Processes the deletion of an existing booking.
     *
     * @param bookingIdToDelete the ID of the booking to delete
     */
    private void processBookingDeletion(Long bookingIdToDelete) {
        try {
            bookingRepository.deleteBooking(bookingIdToDelete);
        } catch (BookingNotFoundException e) {
            throw new BookingNotFoundException("Booking not found by ID: " + bookingIdToDelete);
        }
    }

    /**
     * Converts a list of Booking objects to a list of BookingWithOwnerResourceDTO objects.
     *
     * @param bookings the list of Booking objects to convert
     * @return the list of converted BookingWithOwnerResourceDTO objects
     * @throws UserNotFoundException if the user is not found
     * @throws ResourceNotFoundException if the resource is not found
     */
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
