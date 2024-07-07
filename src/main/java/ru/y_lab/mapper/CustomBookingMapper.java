package ru.y_lab.mapper;

import ru.y_lab.dto.BookingDTO;
import ru.y_lab.dto.BookingWithOwnerResourceDTO;
import ru.y_lab.model.Booking;
import ru.y_lab.model.Resource;
import ru.y_lab.model.User;

import java.time.format.DateTimeFormatter;

public class CustomBookingMapper {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

    public BookingDTO toDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getUserId(),
                booking.getResourceId(),
                booking.getStartTime().format(DATETIME_FORMATTER),
                booking.getEndTime().format(DATETIME_FORMATTER)
        );
    }

    public BookingWithOwnerResourceDTO toBookingWithOwnerResourceDTO(Booking booking, Resource resource, User user) {
        return new BookingWithOwnerResourceDTO(
                user.getId(),
                resource.getId(),
                booking.getId(),
                user.getUsername(),
                resource.getName(),
                resource.getType(),
                booking.getStartTime().toLocalDate().format(DATE_FORMATTER),
                booking.getStartTime().toLocalTime().format(TIME_FORMATTER),
                booking.getEndTime().toLocalTime().format(TIME_FORMATTER)
        );
    }
}
