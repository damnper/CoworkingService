package ru.y_lab.service.impl;

import ru.y_lab.exception.*;
import ru.y_lab.model.Booking;
import ru.y_lab.model.User;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.util.InputReader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The BookingServiceImpl class provides an implementation of the BookingService interface.
 * It interacts with the BookingRepository to perform CRUD operations.
 */
public class BookingServiceImpl implements BookingService {

    private final InputReader inputReader;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ResourceService resourceService;

    /**
     * Constructs a BookingServiceImpl instance with the necessary dependencies.
     * @param inputReader the input reader for user interaction
     * @param bookingRepository the repository managing bookings
     * @param userService the service managing user-related operations
     * @param resourceService the service managing resource-related operations
     */
    public BookingServiceImpl(InputReader inputReader, BookingRepository bookingRepository, UserService userService, ResourceService resourceService) {
        this.inputReader = inputReader;
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.resourceService = resourceService;
    }

    /**
     * Manages bookings by interacting with users and resources.
     * Handles addition, cancellation, updating, and viewing of bookings.
     * @throws BookingConflictException if there is a conflict with an existing booking
     * @throws ResourceNotFoundException if a required resource is not found
     * @throws UserNotFoundException if the current user is not found
     */
    @Override
    public void manageBookings() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        if (userService.getCurrentUser() == null) {
            System.out.println("Access denied. Please log in to manage bookings.");
            throw new UserNotFoundException("Access denied. Please log in to manage bookings.");
        }

        boolean managingBookings = true;
        while (managingBookings) {
            showBookingMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1:
                    resourceService.viewResources();
                    addBooking();
                    break;
                case 2:
                    cancelBooking();
                    break;
                case 3:
                    viewUserBookings();
                    break;
                case 4:
                    updateBooking();
                    break;
                case 5:
                    viewAvailableSlots();
                    break;
                case 0:
                    managingBookings = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Filters bookings based on user input: by date, user ID, or resource ID.
     * @throws ResourceNotFoundException if a required resource is not found
     * @throws UserNotFoundException if the current user is not found
     */
    @Override
    public void filterBookings() throws ResourceNotFoundException, UserNotFoundException {
        String currentUserName = userService.getCurrentUser().getUsername();
        System.out.println("\n--- Filter Bookings ---\n");
        System.out.println("Logged in as: " + currentUserName + "\n");
        System.out.println("1. By Date");
        System.out.println("2. By User");
        System.out.println("3. By Resource");
        System.out.print("Enter your choice: ");
        int choice = getUserChoice();
        switch (choice) {
            case 1:
                System.out.print("Enter date (YYYY-MM-DD): ");
                String dateStr = inputReader.readLine();
                try {
                    LocalDate date = LocalDate.parse(dateStr);
                    List<Booking> bookingsByDate = getBookingsByDate(date);
                    printBookings(bookingsByDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
                }
                break;
            case 2:
                System.out.print("Enter user ID: ");
                String userId = inputReader.readLine();
                List<Booking> bookingsByUser = getBookingsByUser(userId);
                printBookings(bookingsByUser);
                break;
            case 3:
                System.out.print("Enter resource ID: ");
                String resourceId = inputReader.readLine();
                List<Booking> bookingsByResource = getBookingsByResource(resourceId);
                printBookings(bookingsByResource);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
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
    private List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all bookings from the repository for a specific resource.
     * @param resourceId the ID of the resource whose bookings are to be retrieved
     * @return a list of bookings made for the resource with the specified ID
     */
    private List<Booking> getBookingsByResource(String resourceId) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }

    /**
     * Prints details of each booking in the provided list.
     * @param bookings the list of bookings to print details for
     * @throws ResourceNotFoundException if a resource referenced by a booking is not found
     * @throws UserNotFoundException if a user referenced by a booking is not found
     */
    private void printBookings(List<Booking> bookings) throws ResourceNotFoundException, UserNotFoundException {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking booking : bookings) {
                String username = userService.getUserById(booking.getUserId()).getUsername();
                String resourceName = resourceService.getResourceById(booking.getResourceId()).getName();
                System.out.println("Booking ID    : " + booking.getId());
                System.out.println("Resource ID    : " + booking.getResourceId());
                System.out.println("User ID    : " + booking.getUserId());
                System.out.println("User          : " + username);
                System.out.println("Resource Name : " + resourceName);
                System.out.println("Resource Type : " + resourceService.getResourceById(booking.getResourceId()).getType());
                System.out.println("Date    : " + booking.getStartTime().toLocalDate());
                System.out.println("Start Time    : " + booking.getStartTime().toLocalTime());
                System.out.println("End Time      : " + booking.getEndTime().toLocalTime());
                System.out.println("\n----------------------------");
            }
        }
    }

    /**
     * Displays the menu for managing bookings based on the current user's permissions.
     * Shows options for adding, canceling, viewing, and updating bookings,
     * as well as viewing available slots and returning to the main menu.
     * Requires the user to be logged in to display personalized information.
     */
    private void showBookingMenu() {
        String currentUserName = userService.getCurrentUser().getUsername();
        System.out.println("\n--- Manage Bookings ---\n");
        System.out.println("Logged in as: " + currentUserName + "\n");
        System.out.println("1. Add Booking");
        System.out.println("2. Cancel my Booking");
        System.out.println("3. View my Bookings");
        System.out.println("4. Change my Booking");
        System.out.println("5. View Available Slots");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    /**
     * Adds a new booking based on user input.
     * @throws BookingConflictException if there is a conflict with an existing booking
     */
    public void addBooking() throws BookingConflictException {
        System.out.print("Enter resource ID: ");
        String resourceId = inputReader.readLine();

        try {
            resourceService.getResourceById(resourceId);
        } catch (ResourceNotFoundException e) {
            System.out.println("Invalid resource ID. Please try again.");
            return;
        }

        LocalDateTime[] dateTime = getBookingDateTime(resourceId, true);
        if (dateTime == null) {
            return;
        }
        LocalDateTime startDateTime = dateTime[0];
        LocalDateTime endDateTime = dateTime[1];

        // Check for booking conflicts
        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(resourceId);
        for (Booking existingBooking : existingBookings) {
            if (existingBooking.getStartTime().isBefore(endDateTime) && startDateTime.isBefore(existingBooking.getEndTime())) {
                System.out.println("\nBooking conflict: The resource is already booked during this time.");
                throw new BookingConflictException("Booking conflict: The resource is already booked during this time.");
            }
        }

        User currentUser = userService.getCurrentUser();
        Booking booking = new Booking(UUID.randomUUID().toString(), currentUser.getId(), resourceId, startDateTime, endDateTime);
        bookingRepository.addBooking(booking);
        System.out.println("\nBooking added successfully!");
    }

    /**
     * Cancels a booking based on user input.
     */
    public void cancelBooking() {
        System.out.print("Enter booking ID: ");
        String bookingId = inputReader.readLine();

        try {
            Booking booking = bookingRepository.getBookingById(bookingId);
            if (booking == null) {
                System.out.println("Booking not found.");
                return;
            }

            if (!userService.getCurrentUser().getRole().equals("ADMIN") && !booking.getUserId().equals(userService.getCurrentUser().getId())) {
                System.out.println("Permission denied: You can only cancel your own bookings.");
                return;
            }

            bookingRepository.deleteBooking(bookingId);
            System.out.println("\nBooking cancelled successfully!");
        } catch (BookingNotFoundException e) {
            System.out.println("Booking not found: " + e.getMessage());
        }
    }

    /**
     * Views bookings of the currently logged-in user.
     * Retrieves and displays all bookings associated with the user currently logged into the system.
     * If no bookings are found for the user, a message indicating no bookings are available is printed.
     * For each booking found, details including booking ID, booking owner (username),
     * resource name, date, and time are printed to the console.
     *
     * @throws ResourceNotFoundException if a requested resource is not found
     * @throws UserNotFoundException if a user referenced by a booking is not found
     */
    public void viewUserBookings() throws ResourceNotFoundException, UserNotFoundException {
        String currentUserId = userService.getCurrentUser().getId();
        List<Booking> userBookings = bookingRepository.getBookingsByUserId(currentUserId);

        if (userBookings.isEmpty()) {
            System.out.println("\nNo bookings available.");
        } else {
            System.out.println("\n--- My Bookings ---\n");
            for (Booking booking : userBookings) {
                String username = userService.getUserById(booking.getUserId()).getUsername();
                String resourceName = resourceService.getResourceById(booking.getResourceId()).getName();
                System.out.println("Booking ID    : " + booking.getId());
                System.out.println("Booking owner : " + username);
                System.out.println("Resource Name : " + resourceName);
                System.out.println("Date          : " + booking.getStartTime().toLocalDate());
                System.out.println("Time          : " + booking.getStartTime().toLocalTime() + " - " + booking.getEndTime().toLocalTime());
                System.out.println("\n----------------------------");
            }
        }
    }

    /**
     * Updates an existing booking based on user input.
     * @throws BookingConflictException if there is a conflict with an existing booking
     */
    public void updateBooking() throws BookingConflictException {
        System.out.print("Enter booking ID: ");
        String bookingId = inputReader.readLine();

        Booking booking = bookingRepository.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            throw new BookingNotFoundException("Booking not found.");
        }

        if (!userService.getCurrentUser().getRole().equals("ADMIN") && !booking.getUserId().equals(userService.getCurrentUser().getId())) {
            System.out.println("Permission denied: You can only update your own bookings.");
            return;
        }

        LocalDateTime[] dateTime = getBookingDateTime(null, false);
        if (dateTime == null) {
            return;
        }
        LocalDateTime startDateTime = dateTime[0];
        LocalDateTime endDateTime = dateTime[1];

        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(booking.getResourceId());
        for (Booking existingBooking : existingBookings) {
            if (!existingBooking.getId().equals(bookingId) && existingBooking.getStartTime().isBefore(endDateTime) && startDateTime.isBefore(existingBooking.getEndTime())) {
                System.out.println("\nBooking conflict: The resource is already booked during this time.");
                throw new BookingConflictException("Booking conflict: The resource is already booked during this time.");
            }
        }

        if (endDateTime.isBefore(startDateTime)) {
            throw new InvalidBookingTimeException("End time must be after start time. Please try again.");
        }

        booking.setStartTime(startDateTime);
        booking.setEndTime(endDateTime);
        bookingRepository.updateBooking(booking);
        System.out.println("\nBooking updated successfully!");
    }

    /**
     * Views available slots for booking a resource on a specified date.
     */
    public void viewAvailableSlots() {
        System.out.print("Enter resource ID: ");
        String resourceId = inputReader.readLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = inputReader.readLine();

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            return;
        }

        List<Booking> availableSlots = getAvailableSlots(resourceId, date);
        if (availableSlots.isEmpty()) {
            System.out.println("\nNo available slots for the selected date.");
        } else {
            System.out.println("\n--- Available Slots for " + date + " ---\n");
            System.out.println("----------------------------");
            int i = 1;

            for (Booking slot : availableSlots) {
                String startTime = slot.getStartTime().toLocalTime().toString();
                String endTime = slot.getEndTime().toLocalTime().toString();

                System.out.println("Slot " + i + ": " + startTime + " - " + endTime);
                System.out.println("----------------------------");

                i++;
            }
        }
    }

    /**
     * Displays available booking slots for a specified resource and date.
     * Retrieves available slots using the {@link #getAvailableSlots(String, LocalDate)} method
     * and prints them to the console.
     *
     * @param resourceId the ID of the resource for which available slots are displayed
     * @param date the date for which available slots are displayed
     */
    private void viewAvailableSlots(String resourceId, LocalDate date) {
        List<Booking> availableSlots = getAvailableSlots(resourceId, date);
        if (availableSlots.isEmpty()) {
            System.out.println("\nNo available slots for the selected date.");
        } else {
            System.out.println("\n--- Available Slots for " + date + " ---\n");
            System.out.println("----------------------------");
            int i = 1;

            for (Booking slot : availableSlots) {
                String startTime = slot.getStartTime().toLocalTime().toString();
                String endTime = slot.getEndTime().toLocalTime().toString();

                System.out.println("Slot " + i + ": " + startTime + " - " + endTime);
                System.out.println("----------------------------");

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
    private List<Booking> getAvailableSlots(String resourceId, LocalDate date) {
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
    public LocalDateTime[] getBookingDateTime(String resourceId, Boolean flagToViewAvailableSlots) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = inputReader.readLine();

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            throw new InvalidBookingDataException("Invalid date format. Please use YYYY-MM-DD format.");
        }

        if (flagToViewAvailableSlots) {
            viewAvailableSlots(resourceId, date);
        }

        System.out.println("\nBooking can be made only between 09:00 and 18:00.\n");

        System.out.print("Enter start time (HH:MM): ");
        String startTimeStr = inputReader.readLine();
        LocalTime startTime;
        try {
            startTime = LocalTime.parse(startTimeStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format. Please use HH:MM format.");
            throw new InvalidBookingTimeException("Invalid time format. Please use HH:MM format.");
        }

        System.out.print("Enter end time (HH:MM): ");
        String endTimeStr = inputReader.readLine();
        LocalTime endTime;
        try {
            endTime = LocalTime.parse(endTimeStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format. Please use HH:MM format.");
            throw new InvalidBookingTimeException("Invalid time format. Please use HH:MM format.");
        }

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        if (!startDateTime.isBefore(endDateTime)) {
            System.out.println("End time must be after start time. Please try again.");
            throw new InvalidBookingTimeException("End time must be after start time. Please try again.");
        }

        LocalTime startOfDay = LocalTime.of(9, 0);
        LocalTime endOfDay = LocalTime.of(18, 0);
        if (startDateTime.toLocalTime().isBefore(startOfDay) || endDateTime.toLocalTime().isAfter(endOfDay)) {
            System.out.println("Booking must be within working hours (09:00 - 18:00). Please try again.");
            throw new InvalidBookingTimeException("Invalid time format. Please use HH:MM format.");
        }

        return new LocalDateTime[] {startDateTime, endDateTime};
    }

    /**
     * Reads and retrieves the user's choice from input.
     *
     * @return the user's choice as an integer
     */
    public int getUserChoice() {
        try {
            return Integer.parseInt(inputReader.readLine());
        } catch (NumberFormatException e) {
            return -1; // Return -1 for any parsing errors
        }
    }

}
