package ru.y_lab.service.impl;

import ru.y_lab.exception.BookingConflictException;
import ru.y_lab.exception.BookingNotFoundException;
import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Booking;
import ru.y_lab.model.User;
import ru.y_lab.repo.BookingRepository;
import ru.y_lab.service.BookingService;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.y_lab.CoworkingServiceApp.getUserChoice;

/**
 * The BookingServiceImpl class provides an implementation of the BookingService interface.
 * It interacts with the BookingRepository to perform CRUD operations.
 */
public class BookingServiceImpl implements BookingService {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BookingRepository bookingRepository = new BookingRepository();
    private final UserService userService;
    private final ResourceService resourceService;

    public BookingServiceImpl(UserService userService, ResourceService resourceService) {
        this.userService = userService;
        this.resourceService = resourceService;
    }

    @Override
    public void manageBookings() throws BookingConflictException, ResourceNotFoundException, UserNotFoundException {
        if (userService.getCurrentUser() == null) {
            System.out.println("Access denied. Please log in to manage bookings.");
            return;
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
                String dateStr = scanner.nextLine();
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
                String userId = scanner.nextLine();
                List<Booking> bookingsByUser = getBookingsByUser(userId);
                printBookings(bookingsByUser);
                break;
            case 3:
                System.out.print("Enter resource ID: ");
                String resourceId = scanner.nextLine();
                List<Booking> bookingsByResource = getBookingsByResource(resourceId);
                printBookings(bookingsByResource);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private List<Booking> getBookingsByDate(LocalDate date) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsByResource(String resourceId) {
        return bookingRepository.getAllBookings().stream()
                .filter(booking -> booking.getResourceId().equals(resourceId))
                .collect(Collectors.toList());
    }

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

    private void addBooking() throws BookingConflictException {
        System.out.print("Enter resource ID: ");
        String resourceId = scanner.nextLine();

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

    private void cancelBooking() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();

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

    private void viewUserBookings() throws ResourceNotFoundException, UserNotFoundException {
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

    private void updateBooking() {
        System.out.print("Enter booking ID: ");
        String bookingId = scanner.nextLine();

        Booking booking = bookingRepository.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
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

        // Check for booking conflicts
        List<Booking> existingBookings = bookingRepository.getBookingsByResourceId(booking.getResourceId());
        for (Booking existingBooking : existingBookings) {
            if (!existingBooking.getId().equals(bookingId) && existingBooking.getStartTime().isBefore(endDateTime) && startDateTime.isBefore(existingBooking.getEndTime())) {
                System.out.println("\nBooking conflict: The resource is already booked during this time.");
                return;
            }
        }

        booking.setStartTime(startDateTime);
        booking.setEndTime(endDateTime);
        bookingRepository.updateBooking(booking);
        System.out.println("\nBooking updated successfully!");
    }

    private void viewAvailableSlots() {
        System.out.print("Enter resource ID: ");
        String resourceId = scanner.nextLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();

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

    private LocalDateTime[] getBookingDateTime(String resourceId, Boolean flagToViewAvailableSlots) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            return null;
        }

        if (flagToViewAvailableSlots) {
            viewAvailableSlots(resourceId, date);
        }

        System.out.println("\nBooking can be made only between 09:00 and 18:00.\n");

        System.out.print("Enter start time (HH:MM): ");
        String startTimeStr = scanner.nextLine();
        LocalTime startTime;
        try {
            startTime = LocalTime.parse(startTimeStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format. Please use HH:MM format.");
            return null;
        }

        System.out.print("Enter end time (HH:MM): ");
        String endTimeStr = scanner.nextLine();
        LocalTime endTime;
        try {
            endTime = LocalTime.parse(endTimeStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid time format. Please use HH:MM format.");
            return null;
        }

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        if (!startDateTime.isBefore(endDateTime)) {
            System.out.println("End time must be after start time. Please try again.");
            return null;
        }

        LocalTime startOfDay = LocalTime.of(9, 0);
        LocalTime endOfDay = LocalTime.of(18, 0);
        if (startDateTime.toLocalTime().isBefore(startOfDay) || endDateTime.toLocalTime().isAfter(endOfDay)) {
            System.out.println("Booking must be within working hours (09:00 - 18:00). Please try again.");
            return null;
        }

        return new LocalDateTime[] {startDateTime, endDateTime};
    }
}
