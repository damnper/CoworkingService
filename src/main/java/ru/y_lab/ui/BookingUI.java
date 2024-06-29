package ru.y_lab.ui;

import ru.y_lab.exception.ResourceNotFoundException;
import ru.y_lab.exception.UserNotFoundException;
import ru.y_lab.model.Booking;
import ru.y_lab.service.ResourceService;
import ru.y_lab.service.UserService;
import ru.y_lab.util.InputReader;

import java.util.List;

public interface BookingUI {

    void showBookingMenu(UserService userService);

    void showFilterMenu(UserService userService, InputReader inputReader);

    void printBookings(List<Booking> bookings, UserService userService,  ResourceService resourceService) throws UserNotFoundException, ResourceNotFoundException;

    void showUserBooking(List<Booking> bookings, UserService userService, ResourceService resourceService) throws UserNotFoundException, ResourceNotFoundException;
}
