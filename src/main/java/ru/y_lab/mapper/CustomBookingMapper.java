package ru.y_lab.mapper;

import ru.y_lab.dto.BookingDTO;
import ru.y_lab.model.Booking;

public class CustomBookingMapper {

    public Booking toEntity(BookingDTO bookingDTO) {
        if (bookingDTO == null) {
            return null;
        }

        return Booking.builder()
                .id(bookingDTO.getId())
                .userId(bookingDTO.getUserId())
                .resourceId(bookingDTO.getResourceId())
                .startTime(bookingDTO.getStartTime())
                .endTime(bookingDTO.getEndTime())
                .build();
    }

    public BookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setUserId(booking.getUserId());
        bookingDTO.setResourceId(booking.getResourceId());
        bookingDTO.setStartTime(booking.getStartTime());
        bookingDTO.setEndTime(booking.getEndTime());

        return bookingDTO;
    }
}
