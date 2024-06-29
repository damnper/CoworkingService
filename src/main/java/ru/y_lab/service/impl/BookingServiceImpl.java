package ru.y_lab.service.impl;

import lombok.SneakyThrows;
import ru.y_lab.exception.*;
import ru.y_lab.model.Booking;
import ru.y_lab.model.User;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.ui.BookingUI;
import ru.y_lab.util.InputReader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.y_lab.constants.ColorTextCodes.*;

/**
 * The BookingServiceImpl class provides an implementation of the BookingService interface.
 * It interacts with the BookingRepository to perform CRUD operations.
 */
public class BookingServiceImpl implements BookingService {

    private final InputReader inputReader;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ResourceService resourceService;
    private final BookingUI bookingUI;
    private boolean running;

    private final Map<Integer, CheckedRunnable> bookingActions = new HashMap<>();
    private final Map<Integer, CheckedRunnable> filterActions = new HashMap<>();

    @FunctionalInterface
    interface CheckedRunnable {
        void run() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException;
    }

    /**
     * Constructs a BookingServiceImpl instance with the necessary dependencies.
     *
     * @param inputReader       the input reader for user interaction
     * @param bookingRepository the repository managing bookings
     * @param userService       the service managing user-related operations
     * @param resourceService   the service managing resource-related operations
     * @param bookingUI         the UI component for booking interactions
     */
    public BookingServiceImpl(InputReader inputReader, BookingRepository bookingRepository, UserService userService, ResourceService resourceService, BookingUI bookingUI) {
        this.inputReader = inputReader;
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.resourceService = resourceService;
        this.bookingUI = bookingUI;

        bookingActions.put(1, () -> {
            resourceService.viewResources();
            this.addBooking();
        });
        bookingActions.put(2, this::cancelBooking);
        bookingActions.put(3, this::viewUserBookings);
        bookingActions.put(4, this::updateBooking);
        bookingActions.put(5, this::viewAvailableSlots);
        bookingActions.put(0, this::exitApplication);

        filterActions.put(1, this::filterByDate);
        filterActions.put(2, this::filterByUser);
        filterActions.put(3, this::filterByResource);
    }

    /**
     * Manages bookings by interacting with users and resources.
     * Handles addition, cancellation, updating, and viewing of bookings.
     * This method continuously prompts the user for actions until the application exits.
     */
    @Override
    @SneakyThrows
    public void manageBookings() {
        checkUserAccess();

        running = true;
        while (running) {
            bookingUI.showBookingMenu(userService);
            int choice = inputReader.getUserChoice();
            try {
                running = executeChoice(choice);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage() + "\n");
            }
        }
    }


    /**
     * Filters bookings based on user input: by date, user ID, or resource ID.
     * Displays the filter menu and processes the user's choice.
     */
    @Override
    public void filterBookings() {
        bookingUI.showFilterMenu(userService, inputReader);
        int choice = inputReader.getUserChoice();
        try {
            filterActions.getOrDefault(choice, this::invalidChoice).run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Adds a new booking based on user input.
     * Prompts the user to enter the resource ID and booking times.
     */
    @Override
    @SneakyThrows
    public void addBooking() {
        System.out.print(CYAN + "Enter resource ID: " + RESET);
        Long resourceId = Long.parseLong(inputReader.readLine());

        try {
            resourceService.getResourceById(resourceId);
        } catch (ResourceNotFoundException e) {
            System.out.println(RED + "Invalid resource ID. Please try again." + RESET);
            return;
        }

        LocalDateTime[] dateTime = getBookingDateTime(resourceId, true);

        LocalDateTime startDateTime = dateTime[0];
        LocalDateTime endDateTime = dateTime[1];

        // Check for booking conflicts
        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(resourceId);
        for (Booking existingBooking : existingBookings) {
            if (existingBooking.getStartTime().isBefore(endDateTime) && startDateTime.isBefore(existingBooking.getEndTime())) {
                throw new BookingConflictException("Booking conflict: The resource is already booked during this time.");
            }
        }

        User currentUser = userService.getCurrentUser();

        Booking booking = Booking.builder()
                .userId(currentUser.getId())
                .resourceId(resourceId)
                .startTime(startDateTime)
                .endTime(endDateTime)
                .build();

        bookingRepository.addBooking(booking);
        System.out.println(GREEN + "\nBooking added successfully!" + RESET);
    }

    /**
     * Cancels a booking based on user input.
     * Prompts the user to enter the booking ID and validates the user's access rights.
     */
    @Override
    public void cancelBooking() {
        System.out.print(CYAN + "Enter booking ID: " + RESET);
        Long bookingId = Long.parseLong(inputReader.readLine());

        Booking booking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(YELLOW + "Booking with ID " + bookingId + " not found" + RESET));

        if (checkBookingAccess(booking, RED + "Permission denied: You can only cancel your own bookings." + RESET))
            return;

        bookingRepository.deleteBooking(bookingId);
        System.out.println(GREEN + "\nBooking cancelled successfully!" + RESET);
    }

    /**
     * Views bookings of the currently logged-in user.
     * Retrieves and displays all bookings associated with the user currently logged into the system.
     */
    @Override
    @SneakyThrows
    public void viewUserBookings() {
        Long currentUserId = userService.getCurrentUser().getId();
        List<Booking> userBookings = bookingRepository.getBookingsByUserId(currentUserId);

        if (userBookings.isEmpty()) {
            System.out.println(YELLOW + "\nNo bookings available." + RESET);
        } else {
            bookingUI.showUserBooking(userBookings, userService, resourceService);
        }
    }

    /**
     * Updates an existing booking based on user input.
     * Prompts the user to enter the booking ID and new booking times.
     */
    @SneakyThrows
    @Override
    public void updateBooking() {
        System.out.print(CYAN + "Enter booking ID: " + RESET);
        Long bookingId = Long.parseLong(inputReader.readLine());

        Booking booking = bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found"));

        if (checkBookingAccess(booking, "Permission denied: You can only update your own bookings.")) return;

        LocalDateTime[] dateTime = getBookingDateTime(null, false);

        LocalDateTime startDateTime = dateTime[0];
        LocalDateTime endDateTime = dateTime[1];

        isBookingConflict(booking, bookingId, endDateTime, startDateTime);

        if (endDateTime.isBefore(startDateTime)) {
            throw new InvalidBookingTimeException("End time must be after start time. Please try again.");
        }

        booking.setStartTime(startDateTime);
        booking.setEndTime(endDateTime);
        bookingRepository.updateBooking(booking);
        System.out.println(GREEN + "\nBooking updated successfully!" + RESET);
    }

    /**
     * Views available slots for booking a resource on a specified date.
     * Prompts the user to enter the resource ID and date, then displays the available slots.
     */
    @Override
    public void viewAvailableSlots() {
        System.out.print(CYAN + "Enter resource ID: " + RESET);
        Long resourceId = Long.parseLong(inputReader.readLine());
        System.out.print(CYAN + "Enter date (YYYY-MM-DD): " + RESET);
        String dateStr = inputReader.readLine();

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println(YELLOW + "Invalid date format. Please use YYYY-MM-DD format." + RESET);
            return;
        }

        List<Booking> availableSlots = getAvailableSlots(resourceId, date);
        if (availableSlots.isEmpty()) {
            System.out.println(YELLOW + "\nNo available slots for the selected date." + RESET);
        } else {
            System.out.println(BLUE + "\n--- Available Slots for " + date + " ---\n" + RESET);
            System.out.println(CYAN + "----------------------------" + RESET);
            int i = 1;

            for (Booking slot : availableSlots) {
                String startTime = slot.getStartTime().toLocalTime().toString();
                String endTime = slot.getEndTime().toLocalTime().toString();

                System.out.println(PURPLE + "Slot " + i + ": " + startTime + " - " + endTime  + RESET);
                System.out.println(CYAN + "----------------------------" + RESET);
                i++;
            }
        }
    }

    /**
     * Displays available booking slots for a specified resource and date.
     * Retrieves available slots using the {@link #getAvailableSlots(Long, LocalDate)} method
     * and prints them to the console.
     *
     * @param resourceId the ID of the resource for which available slots are displayed
     * @param date the date for which available slots are displayed
     */
    private void viewAvailableSlots(Long resourceId, LocalDate date) {
        List<Booking> availableSlots = getAvailableSlots(resourceId, date);
        if (availableSlots.isEmpty()) {
            System.out.println(YELLOW + "\nNo available slots for the selected date." + RESET);
        } else {
            System.out.println(BLUE + "\n--- Available Slots for " + date + " ---\n" + RESET);
            System.out.println(CYAN + "----------------------------" + RESET);
            int i = 1;

            for (Booking slot : availableSlots) {
                String startTime = slot.getStartTime().toLocalTime().toString();
                String endTime = slot.getEndTime().toLocalTime().toString();

                System.out.println(PURPLE + "Slot " + i + ": " + startTime + " - " + endTime + RESET);
                System.out.println(CYAN + "----------------------------" + RESET);
                i++;
            }
        }
    }

    /**
     * Retrieves available booking slots for a resource on a specified date.
     * Filters existing bookings by date and calculates available time slots.
     *
     * @param resourceId the ID of the resource for which available slots are retrieved
     * @param date the date for which available slots are retrieved
     * @return a list of Booking objects representing available time slots
     */
    private List<Booking> getAvailableSlots(Long resourceId, LocalDate date) {
        List<Booking> bookings = bookingRepository.getBookingsByResourceId(resourceId);
        List<Booking> availableSlots = new ArrayList<>();

        List<Booking> filteredBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStartTime().toLocalDate().equals(date)) {
                filteredBookings.add(booking);
            }
        }

        filteredBookings.sort(Comparator.comparing(Booking::getStartTime));

        LocalDateTime startOfDay = date.atTime(LocalTime.of(9, 0));
        LocalDateTime endOfDay = date.atTime(LocalTime.of(18, 0));

        LocalDateTime slotStart = startOfDay;

        for (Booking booking : filteredBookings) {
            LocalDateTime bookingStart = booking.getStartTime();
            LocalDateTime bookingEnd = booking.getEndTime();

            if (slotStart.isBefore(bookingStart)) {
                availableSlots.add(new Booking(null, null, resourceId, slotStart, bookingStart));
            }

            slotStart = bookingEnd;
        }

        if (slotStart.isBefore(endOfDay)) {
            availableSlots.add(new Booking(null, null, resourceId, slotStart, endOfDay));
        }

        return availableSlots;
    }

    /**
     * Prompts the user to input date, start time, and end time for booking a resource.
     * Validates input formats and checks if the booking time falls within working hours.
     * Throws exceptions for invalid input formats or booking times.
     *
     * @param resourceId the ID of the resource to be booked (optional, used for viewing available slots)
     * @param flagToViewAvailableSlots whether to display available slots before booking
     * @return an array of LocalDateTime containing startDateTime and endDateTime for the booking
     * @throws InvalidBookingDataException if the date format is invalid
     * @throws InvalidBookingTimeException if the time format is invalid or booking time is outside working hours
     */
    private LocalDateTime[] getBookingDateTime(Long resourceId, Boolean flagToViewAvailableSlots) {
        LocalDate date = readDate();

        if (flagToViewAvailableSlots) {
            viewAvailableSlots(resourceId, date);
        }

        System.out.println(YELLOW + "\nBooking can be made only between 09:00 and 18:00.\n"  + RESET);

        LocalTime startTime = readAndValidateTime("Enter start time (HH:MM): ");
        LocalTime endTime = readAndValidateTime("Enter end time (HH:MM): ");

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        validateBookingTimes(startDateTime, endDateTime);

        return new LocalDateTime[]{startDateTime, endDateTime};
    }

    /**
     * Reads and validates the date input by the user.
     *
     * @return the validated LocalDate object representing the date
     */
    private LocalDate readDate() {
        System.out.print(CYAN + "Enter date (YYYY-MM-DD): "  + RESET);
        String dateStr = inputReader.readLine();

        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidBookingDataException("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    /**
     * Prompts the user to enter a time and validates the format.
     *
     * @param prompt the prompt message to display to the user
     * @return the validated LocalTime object representing the time
     * @throws InvalidBookingTimeException if the time format is invalid
     */
    private LocalTime readAndValidateTime(String prompt) {
        while (true) {
            System.out.print(CYAN + prompt  + RESET);
            String timeStr = inputReader.readLine();

            try {
                return LocalTime.parse(timeStr);
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Invalid time format. Please use HH:MM format." + RESET);
            }
        }
    }

    /**
     * Validates that the booking start and end times fall within working hours (09:00 - 18:00).
     *
     * @param startDateTime the LocalDateTime representing the booking start time
     * @param endDateTime the LocalDateTime representing the booking end time
     * @throws InvalidBookingTimeException if the booking time is outside working hours
     */
    private void validateBookingTimes(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalTime startOfDay = LocalTime.of(9, 0);
        LocalTime endOfDay = LocalTime.of(18, 0);

        if (startDateTime.toLocalTime().isBefore(startOfDay) || endDateTime.toLocalTime().isAfter(endOfDay)) {
            throw new InvalidBookingTimeException("Booking must be within working hours (09:00 - 18:00).");
        }

        if (!startDateTime.isBefore(endDateTime)) {
            System.out.println(RED + "End time must be after start time. Please try again." + RESET);
        }
    }

    /**
     * Retrieves all bookings from the repository that match the specified date.
     * @param date the date to filter bookings by
     * @return a list of bookings that occur on the specified date
     */
    private List<Booking> getBookingsByDate(LocalDate date) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bookings from the repository made by a specific user.
     * @param userId the ID of the user whose bookings are to be retrieved
     * @return a list of bookings made by the user with the specified ID
     */
    private List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bookings from the repository for a specific resource.
     * @param resourceId the ID of the resource whose bookings are to be retrieved
     * @return a list of bookings made for the resource with the specified ID
     */
    private List<Booking> getBookingsByResource(Long resourceId) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }

    /**
     * Filters bookings by a specified date.
     * Prompts the user to enter a date and displays bookings for that date.
     * Handles exceptions for invalid date formats.
     */
    private void filterByDate() {
        System.out.print(CYAN + "Enter date (YYYY-MM-DD): " + RESET);
        String dateStr = inputReader.readLine();
        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<Booking> bookingsByDate = getBookingsByDate(date);
            bookingUI.printBookings(bookingsByDate, userService, resourceService);
        } catch (DateTimeParseException | UserNotFoundException | ResourceNotFoundException e) {
            System.out.println(RED + "Invalid date format. Please use YYYY-MM-DD format." + RESET);
        }
    }

    /**
     * Filters bookings by a specified user ID.
     * Prompts the user to enter a user ID and displays bookings made by that user.
     * Handles exceptions for user not found scenarios.
     *
     * @throws UserNotFoundException if the specified user ID does not exist
     * @throws ResourceNotFoundException if a referenced resource is not found
     */
    private void filterByUser() throws UserNotFoundException, ResourceNotFoundException {
        System.out.print(CYAN + "Enter user ID: " + RESET);
        Long userId = Long.parseLong(inputReader.readLine());
        List<Booking> bookingsByUser = getBookingsByUser(userId);
        bookingUI.printBookings(bookingsByUser, userService, resourceService);
    }

    /**
     * Filters bookings by a specified resource ID.
     * Prompts the user to enter a resource ID and displays bookings for that resource.
     * Handles exceptions for resource not found scenarios.
     *
     * @throws UserNotFoundException if a referenced user is not found
     * @throws ResourceNotFoundException if the specified resource ID does not exist
     */
    private void filterByResource() throws UserNotFoundException, ResourceNotFoundException {
        System.out.print(CYAN + "Enter resource ID: " + RESET);
        Long resourceId = Long.parseLong(inputReader.readLine());
        List<Booking> bookingsByResource = getBookingsByResource(resourceId);
        bookingUI.printBookings(bookingsByResource, userService, resourceService);
    }

    /**
     * Checks if the current user has access to the specified booking.
     * If the user is not an admin and does not own the booking, displays an error message and returns true.
     *
     * @param booking the booking to check access for
     * @param message the error message to display if access is denied
     * @return true if access is denied, false otherwise
     */
    private boolean checkBookingAccess(Booking booking, String message) {
        if (!userService.getCurrentUser().getRole().equals("ADMIN") && !booking.getUserId().equals(userService.getCurrentUser().getId())) {
            System.out.println(RED + message + RESET);
            return true;
        }
        return false;
    }

    /**
     * Checks if the current user is logged in.
     * If not, displays an error message and throws a UserNotFoundException.
     *
     * @throws UserNotFoundException if no user is currently logged in
     */
    private void checkUserAccess() throws UserNotFoundException {
        if (userService.getCurrentUser() == null) {
            System.out.println(RED + "\nAccess denied. Please log in to manage resources." + RESET);
            throw new UserNotFoundException("Access denied. Please log in to manage bookings.");
        }
    }

    /**
     * Checks for booking conflicts with existing bookings for a specified resource.
     * If a conflict is found, throws a BookingConflictException.
     *
     * @param booking the booking being checked
     * @param bookingId the ID of the booking being checked
     * @param endDateTime the end time of the booking being checked
     * @param startDateTime the start time of the booking being checked
     */
    @SneakyThrows
    private void isBookingConflict(Booking booking, Long bookingId, LocalDateTime endDateTime, LocalDateTime startDateTime) {
        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(booking.getResourceId());
        for (Booking existingBooking : existingBookings) {
            if (!existingBooking.getId().equals(bookingId) && existingBooking.getStartTime().isBefore(endDateTime) && startDateTime.isBefore(existingBooking.getEndTime())) {
                throw new BookingConflictException("Booking conflict: The resource is already booked during this time.");
            }
        }
    }

    /**
     * Executes the chosen action based on the user's input.
     *
     * @param choice the integer corresponding to the user's chosen action
     * @return true if the application should continue running, false if it should exit
     */
    private boolean executeChoice(int choice) throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        CheckedRunnable action = getActionsMap().getOrDefault(choice, this::invalidChoice);
        action.run();
        return running;
    }

    /**
     * Determines the appropriate actions map based on the user's role.
     *
     * @return the map of actions for the current user
     */
    private Map<Integer, CheckedRunnable> getActionsMap() {
        return bookingActions;
    }

    /**
     * Sets running to false to exit the application.
     */
    private void exitApplication() {
        running = false;
    }

    /**
     * Displays an invalid choice message.
     */
    private void invalidChoice() {
        System.out.println("Invalid choice. Please try again.");
    }

}
