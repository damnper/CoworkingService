//package ru.y_lab.service.impl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import ru.y_lab.dto.BookingDTO;
//import ru.y_lab.exception.BookingConflictException;
//import ru.y_lab.exception.BookingNotFoundException;
//import ru.y_lab.mapper.CustomBookingMapper;
//import ru.y_lab.model.Booking;
//import ru.y_lab.repo.BookingRepository;
//import ru.y_lab.service.BookingService;
//import ru.y_lab.service.ResourceService;
//import ru.y_lab.service.UserService;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * The BookingServiceImpl class provides an implementation of the BookingService interface.
// * It interacts with the BookingRepository to perform CRUD operations.
// */
//public class BookingServiceImpl implements BookingService {
//
//    private final BookingRepository bookingRepository = new BookingRepository();
//    private final UserService userService = new UserServiceImpl();
//    private final ResourceService resourceService = new ResourceServiceImpl();
//    private final ObjectMapper objectMapper = new ObjectMapper();
////    private final BookingMapper bookingMapper = BookingMapper.INSTANCE;
//    private final CustomBookingMapper bookingMapper = new CustomBookingMapper();
//
//
//    /**
//     * Adds a new booking based on user input.
//     * Prompts the user to enter the resource ID and booking times.
//     */
//    @Override
//    @SneakyThrows
//    public BookingDTO addBooking(String bookingJson) {
//        BookingDTO bookingDTO = objectMapper.readValue(bookingJson, BookingDTO.class);
//        validateBookingDTO(bookingDTO);
//
//        resourceService.getResourceById(bookingDTO.getResourceId()); // Проверка на существование ресурса
//
//        Booking booking = bookingMapper.toEntity(bookingDTO);
//        booking.setUserId(userService.getCurrentUser().getId());
//
//        checkBookingConflicts(booking);
//
//        bookingRepository.addBooking(booking);
//        return bookingMapper.toDTO(booking);
//    }
//
//
//    /**
//     * Updates an existing booking based on user input.
//     * Prompts the user to enter the booking ID and new booking times.
//     */
//    @SneakyThrows
//    @Override
//    public void updateBooking(String bookingJson) {
//        BookingDTO bookingDTO = objectMapper.readValue(bookingJson, BookingDTO.class);
//        validateBookingDTO(bookingDTO);
//
//        Booking existingBooking = bookingRepository.getBookingById(bookingDTO.getId())
//                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingDTO.getId() + " not found"));
//
//        if (!checkBookingAccess(existingBooking)) {
//            throw new SecurityException("Permission denied: You can only update your own bookings.");
//        }
//
//        existingBooking.setStartTime(bookingDTO.getStartTime());
//        existingBooking.setEndTime(bookingDTO.getEndTime());
//
//        checkBookingConflicts(existingBooking);
//
//        bookingRepository.updateBooking(existingBooking);
//    }
//
//    @Override
//    @SneakyThrows
//    public BookingDTO getBookingById(Long bookingId) {
//        Booking booking = bookingRepository.getBookingById(bookingId)
//                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found"));
//
//        return bookingMapper.toDTO(booking);
//    }
//
//    /**
//     * Views bookings of the currently logged-in user.
//     * Retrieves and displays all bookings associated with the user currently logged into the system.
//     */
//    @Override
//    @SneakyThrows
//    public List<BookingDTO> viewUserBookings() {
//        Long currentUserId = userService.getCurrentUser().getId();
//        List<Booking> bookings = bookingRepository.getBookingsByUserId(currentUserId);
//
//        return bookings.stream().map(bookingMapper::toDTO).collect(Collectors.toList());
//    }
//
//    /**
//     * Cancels a booking based on user input.
//     * Prompts the user to enter the booking ID and validates the user's access rights.
//     */
//    @Override
//    @SneakyThrows
//    public void deleteBooking(Long bookingId) {
//        Booking booking = bookingRepository.getBookingById(bookingId)
//                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found"));
//
//        if (!checkBookingAccess(booking)) {
//            throw new SecurityException("Permission denied: You can only delete your own bookings.");
//        }
//
//        bookingRepository.deleteBooking(bookingId);
//    }
//
//    /**
//     * Filters bookings based on user input: by date, user ID, or resource ID.
//     * Displays the filter menu and processes the user's choice.
//     */
//    @Override
//    @SneakyThrows
//    public List<BookingDTO> filterBookings(String filterJson) {
//        BookingDTO filterDTO = objectMapper.readValue(filterJson, BookingDTO.class);
//
//        List<Booking> filteredBookings = bookingRepository.getAllBookings().stream()
//                .filter(booking -> matchesFilter(booking, filterDTO))
//                .toList();
//
//        return filteredBookings.stream().map(bookingMapper::toDTO).collect(Collectors.toList());
//    }
//
//    /**
//     * Views available slots for booking a resource on a specified date.
//     * Prompts the user to enter the resource ID and date, then displays the available slots.
//     */
//    @Override
//    @SneakyThrows
//    public List<BookingDTO> viewAvailableSlots(Long resourceId, String dateStr) {
//        LocalDate date;
//        try {
//            date = LocalDate.parse(dateStr);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD format.");
//        }
//
//        List<Booking> availableSlots = getAvailableSlots(resourceId, date);
//        return availableSlots.stream().map(bookingMapper::toDTO).collect(Collectors.toList());
//    }
//
//    private boolean matchesFilter(Booking booking, BookingDTO filterDTO) {
//        boolean matches = true;
//
//        if (filterDTO.getUserId() != null) {
//            matches = booking.getUserId().equals(filterDTO.getUserId());
//        }
//        if (filterDTO.getResourceId() != null) {
//            matches = matches && booking.getResourceId().equals(filterDTO.getResourceId());
//        }
//        if (filterDTO.getStartTime() != null) {
//            matches = matches && booking.getStartTime().toLocalDate().equals(filterDTO.getStartTime().toLocalDate());
//        }
//
//        return matches;
//    }
//
//    private void validateBookingDTO(BookingDTO bookingDTO) {
//        if (bookingDTO.getStartTime() == null || bookingDTO.getEndTime() == null) {
//            throw new IllegalArgumentException("Start time and end time must be provided.");
//        }
//        if (!bookingDTO.getStartTime().isBefore(bookingDTO.getEndTime())) {
//            throw new IllegalArgumentException("Start time must be before end time.");
//        }
//    }
//
//    private boolean checkBookingAccess(Booking booking) {
//        return userService.getCurrentUser().getRole().equals("ADMIN") ||
//                booking.getUserId().equals(userService.getCurrentUser().getId());
//    }
//
//    private void checkBookingConflicts(Booking booking) throws BookingConflictException {
//        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(booking.getResourceId());
//
//        for (Booking existingBooking : existingBookings) {
//            if (existingBooking.getStartTime().isBefore(booking.getEndTime()) &&
//                    booking.getStartTime().isBefore(existingBooking.getEndTime())) {
//                throw new BookingConflictException("Booking conflict: The resource is already booked during this time.");
//            }
//        }
//    }
//
//    /**
//     * Retrieves available booking slots for a resource on a specified date.
//     * Filters existing bookings by date and calculates available time slots.
//     *
//     * @param resourceId the ID of the resource for which available slots are retrieved
//     * @param date the date for which available slots are retrieved
//     * @return a list of Booking objects representing available time slots
//     */
//    private List<Booking> getAvailableSlots(Long resourceId, LocalDate date) {
//        List<Booking> bookings = bookingRepository.getBookingsByResourceId(resourceId);
//        List<Booking> availableSlots = new ArrayList<>();
//
//        List<Booking> filteredBookings = new ArrayList<>();
//        for (Booking booking : bookings) {
//            if (booking.getStartTime().toLocalDate().equals(date)) {
//                filteredBookings.add(booking);
//            }
//        }
//
//        filteredBookings.sort(Comparator.comparing(Booking::getStartTime));
//
//        LocalDateTime startOfDay = date.atTime(LocalTime.of(9, 0));
//        LocalDateTime endOfDay = date.atTime(LocalTime.of(18, 0));
//
//        LocalDateTime slotStart = startOfDay;
//
//        for (Booking booking : filteredBookings) {
//            LocalDateTime bookingStart = booking.getStartTime();
//            LocalDateTime bookingEnd = booking.getEndTime();
//
//            if (slotStart.isBefore(bookingStart)) {
//                availableSlots.add(new Booking(null, null, resourceId, slotStart, bookingStart));
//            }
//
//            slotStart = bookingEnd;
//        }
//
//        if (slotStart.isBefore(endOfDay)) {
//            availableSlots.add(new Booking(null, null, resourceId, slotStart, endOfDay));
//        }
//
//        return availableSlots;
//    }
//
//    /**
//     * Retrieves all bookings from the repository that match the specified date.
//     * @param date the date to filter bookings by
//     * @return a list of bookings that occur on the specified date
//     */
//    private List<Booking> getBookingsByDate(LocalDate date) {
//        return bookingRepository.getAllBookings().stream()
//                .filter(booking -> booking.getStartTime().toLocalDate().equals(date))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Retrieves all bookings from the repository made by a specific user.
//     * @param userId the ID of the user whose bookings are to be retrieved
//     * @return a list of bookings made by the user with the specified ID
//     */
//    private List<Booking> getBookingsByUser(Long userId) {
//        return bookingRepository.getAllBookings().stream()
//                .filter(booking -> booking.getUserId().equals(userId))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Retrieves all bookings from the repository for a specific resource.
//     * @param resourceId the ID of the resource whose bookings are to be retrieved
//     * @return a list of bookings made for the resource with the specified ID
//     */
//    private List<Booking> getBookingsByResource(Long resourceId) {
//        return bookingRepository.getAllBookings().stream()
//                .filter(booking -> booking.getResourceId().equals(resourceId))
//                .collect(Collectors.toList());
//    }
//
//}
