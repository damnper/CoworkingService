package ru.y_lab.mapper;

import ru.y_lab.dto.BookingDTO;
import ru.y_lab.model.Booking;

public class CustomBookingMapper {

    public Booking toEntity(BookingDTO bookingDTO) {
        if (bookingDTO == null) {
            return null;
        }

        return Booking.builder()
                .id(bookingDTO.id())
                .userId(bookingDTO.userId())
                .resourceId(bookingDTO.resourceId())
                .startTime(bookingDTO.startTime())
                .endTime(bookingDTO.endTime())
                .build();
    }

    public BookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingDTO(
                booking.getId(),
                booking.getUserId(),
                booking.getResourceId(),
                booking.getStartTime(),
                booking.getEndTime()
        );
    }
}
