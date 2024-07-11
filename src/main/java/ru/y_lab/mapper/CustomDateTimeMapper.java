package ru.y_lab.mapper;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Component
public class CustomDateTimeMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_TIME;

    public LocalDateTime toLocalDateTime(Long dateTime) {
        return dateTime != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneOffset.UTC)
                : null;
    }

    public LocalDate toLocalDate(Long date) {
        return date != null
                ? LocalDate.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC)
                : null;
    }

    public static String formatLocalTime(LocalTime dateTime) {
        return dateTime.format(FORMATTER);
    }

}
