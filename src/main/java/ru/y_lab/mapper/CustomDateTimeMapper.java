package ru.y_lab.mapper;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * A utility class for converting between different date and time representations.
 * This class provides methods to convert epoch milliseconds to {@link LocalDateTime} and {@link LocalDate},
 * as well as formatting {@link LocalTime} to a string representation.
 */
@Component
public class CustomDateTimeMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_TIME;

    /**
     * Converts epoch milliseconds to {@link LocalDateTime}.
     *
     * @param dateTime the epoch milliseconds to convert
     * @return the corresponding {@link LocalDateTime}, or null if the input is null
     */
    public LocalDateTime toLocalDateTime(Long dateTime) {
        return dateTime != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneOffset.UTC)
                : null;
    }

    /**
     * Converts epoch milliseconds to {@link LocalDate}.
     *
     * @param date the epoch milliseconds to convert
     * @return the corresponding {@link LocalDate}, or null if the input is null
     */
    public LocalDate toLocalDate(Long date) {
        return date != null
                ? LocalDate.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC)
                : null;
    }

    /**
     * Formats a {@link LocalTime} to an ISO-8601 time string.
     *
     * @param dateTime the {@link LocalTime} to format
     * @return the formatted time string
     */
    public static String formatLocalTime(LocalTime dateTime) {
        return dateTime.format(FORMATTER);
    }

}
