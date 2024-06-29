package ru.y_lab.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTests {

    @Test
    public void testBookingConstructorAndGetters() {
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 29, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 29, 12, 0);
        Booking booking = new Booking(1L, 2L, 3L, startTime, endTime);

        assertEquals(1L, booking.getId());
        assertEquals(2L, booking.getUserId());
        assertEquals(3L, booking.getResourceId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
    }

    @Test
    public void testBookingSetters() {
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 29, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 29, 12, 0);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(2L);
        booking.setResourceId(3L);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);

        assertEquals(1L, booking.getId());
        assertEquals(2L, booking.getUserId());
        assertEquals(3L, booking.getResourceId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
    }

    @Test
    public void testBookingBuilder() {
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 29, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 29, 12, 0);
        Booking booking = Booking.builder()
                .id(4L)
                .userId(5L)
                .resourceId(6L)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        assertEquals(4L, booking.getId());
        assertEquals(5L, booking.getUserId());
        assertEquals(6L, booking.getResourceId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
    }

    @Test
    public void testBookingEqualsAndHashCode() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 6, 29, 10, 0);
        LocalDateTime endTime1 = LocalDateTime.of(2024, 6, 29, 12, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 6, 30, 14, 0);
        LocalDateTime endTime2 = LocalDateTime.of(2024, 6, 30, 16, 0);

        Booking booking1 = new Booking(1L, 2L, 3L, startTime1, endTime1);
        Booking booking2 = new Booking(1L, 2L, 3L, startTime1, endTime1);
        Booking booking3 = new Booking(2L, 3L, 4L, startTime2, endTime2);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertNotEquals(booking2, booking3);

        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1.hashCode(), booking3.hashCode());
    }

    @Test
    public void testBookingToString() {
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 29, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 6, 29, 12, 0);
        Booking booking = new Booking(1L, 2L, 3L, startTime, endTime);
        String expectedString = "Booking(id=1, userId=2, resourceId=3, startTime=2024-06-29T10:00, endTime=2024-06-29T12:00)";
        assertEquals(expectedString, booking.toString());
    }

    @Test
    public void testBookingDefaultConstructor() {
        Booking booking = new Booking();
        assertNull(booking.getId());
        assertNull(booking.getUserId());
        assertNull(booking.getResourceId());
        assertNull(booking.getStartTime());
        assertNull(booking.getEndTime());
    }

    @Test
    public void testBookingEqualsWithDifferentFields() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 6, 29, 10, 0);
        LocalDateTime endTime1 = LocalDateTime.of(2024, 6, 29, 12, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 6, 30, 14, 0);

        Booking booking1 = new Booking(1L, 2L, 3L, startTime1, endTime1);
        Booking booking2 = new Booking(1L, 2L, 3L, startTime2, endTime1);

        assertNotEquals(booking1, booking2);
    }
}
