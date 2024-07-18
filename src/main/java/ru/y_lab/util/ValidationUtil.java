package ru.y_lab.util;

import org.springframework.stereotype.Component;
import ru.y_lab.dto.UpdateBookingRequestDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Utility class for handling data validation.
 */
@Component
public class ValidationUtil {

    private static final String INVALID_TIME_MESSAGE = "Invalid time. Start time and end time must be in the future.";
    private static final String INVALID_START_TIME_MESSAGE = "Start time must be provided and must be a Long.";
    private static final String INVALID_END_TIME_MESSAGE = "End time must be provided and must be a Long.";
    private static final String INVALID_START_BEFORE_END_MESSAGE = "Start time must be before end time.";
    private static final String INVALID_TIME_IN_PAST_MESSAGE = "Start time and end time must be in the future.";

    /**
     * Validates the UpdateBookingRequestDTO for resourceId, startTime, and endTime.
     *
     * @param request the UpdateBookingRequestDTO to validate
     */
    public static void validateUpdateBookingRequest(UpdateBookingRequestDTO request) {
        validateTime(request.startTime(), request.endTime());
    }

    /**
     * Validates the date and time of booking.
     *
     * @param startDateTime the start date and time of booking
     * @param endDateTime the end date and time of booking
     */
    public static void validateDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException(INVALID_TIME_MESSAGE);
        }
        if (!startDateTime.isBefore(endDateTime)) {
            throw new IllegalArgumentException(INVALID_START_BEFORE_END_MESSAGE);
        }
        if (startDateTime.isBefore(LocalDateTime.now()) | endDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(INVALID_TIME_IN_PAST_MESSAGE);
        }
    }

    private static void validateTime(Long startTime, Long endTime) {
        if (startTime == null) {
            throw new IllegalArgumentException(INVALID_START_TIME_MESSAGE);
        }
        if (endTime == null) {
            throw new IllegalArgumentException(INVALID_END_TIME_MESSAGE);
        }
        LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneOffset.UTC);
        LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneOffset.UTC);
        validateDateTime(startDateTime, endDateTime);
    }
}
