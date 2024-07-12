package ru.y_lab.util;

import org.springframework.stereotype.Component;
import ru.y_lab.dto.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Pattern;

/**
 * Utility class for handling data validation.
 */
@Component
public class ValidationUtil {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,15}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9@#%]{6,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-Я0-9 ]{1,50}$");

    private static final String INVALID_USERNAME_MESSAGE = "Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.";
    private static final String INVALID_PASSWORD_MESSAGE = "Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %.";
    private static final String INVALID_NAME_MESSAGE = "Invalid resourceName. Name must be 1 to 50 characters long and can only contain letters (Latin and Cyrillic).";
    private static final String INVALID_TIME_MESSAGE = "Invalid time. Start time and end time must be in the future.";
    private static final String INVALID_RESOURCE_ID_MESSAGE = "Resource ID must be provided and must be a Long.";
    private static final String INVALID_START_TIME_MESSAGE = "Start time must be provided and must be a Long.";
    private static final String INVALID_END_TIME_MESSAGE = "End time must be provided and must be a Long.";
    private static final String INVALID_START_BEFORE_END_MESSAGE = "Start time must be before end time.";
    private static final String INVALID_TIME_IN_PAST_MESSAGE = "Start time and end time must be in the future.";

    /**
     * Validates the username based on predefined rules.
     * @param username the username to be validated
     * @return true if the username is valid, false otherwise
     */
    public static boolean validateUsername(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates the password based on predefined rules.
     * @param password the password to be validated
     * @return true if the password is valid, false otherwise
     */
    public static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates the RegisterRequestDTO for username and password.
     *
     * @param request the RegisterRequestDTO to validate
     */
    public static void validateRegisterRequest(RegisterRequestDTO request) {
        if (!validateUsername(request.username())) {
            throw new IllegalArgumentException(INVALID_USERNAME_MESSAGE);
        }
        if (!validatePassword(request.password())) {
            throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);
        }
    }

    /**
     * Validates the LoginRequestDTO for username and password.
     *
     * @param request the LoginRequestDTO to validate
     */
    public static void validateLoginRequest(LoginRequestDTO request) {
        if (request.username() == null || request.username().isEmpty() || !validateUsername(request.username())) {
                throw new IllegalArgumentException(INVALID_USERNAME_MESSAGE);
        }
        if (request.password() == null || request.password().isEmpty() || !validatePassword(request.password())) {
            throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);
        }
    }

    /**
     * Validates the UpdateUserRequestDTO for username and password.
     *
     * @param request the UpdateUserRequestDTO to validate
     */
    public static void validateUpdateUserRequest(UpdateUserRequestDTO request) {
        if (request.username() == null || request.username().isEmpty() || !validateUsername(request.username())) {
            throw new IllegalArgumentException(INVALID_USERNAME_MESSAGE);
        }
        if (request.password() == null || request.password().isEmpty() || !validatePassword(request.password())) {
            throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);
        }
    }

    /**
     * Validates the AddResourceRequestDTO for resourceName.
     *
     * @param request the AddResourceRequestDTO to validate
     */
    public static void validateAddResourceRequest(AddResourceRequestDTO request) {
        if (request.name() == null || request.name().isEmpty() || !validateName(request.name())) {
            throw new IllegalArgumentException(INVALID_NAME_MESSAGE);
        }
    }

    /**
     * Validates the UpdateResourceRequestDTO for resourceName and type.
     *
     * @param request the UpdateResourceRequestDTO to validate
     */
    public static void validateUpdateResourceRequest(UpdateResourceRequestDTO request) {
        if (request.name() == null || request.name().isEmpty() || !validateName(request.name())) {
            throw new IllegalArgumentException(INVALID_NAME_MESSAGE);
        }
    }

    /**
     * Validates the resourceName based on predefined rules.
     * @param name the resourceName to be validated
     * @return true if the resourceName is valid, false otherwise
     */
    public static boolean validateName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validates the AddBookingRequestDTO for resourceId, startTime, and endTime.
     *
     * @param request the AddBookingRequestDTO to validate
     */
    public static void validateAddBookingRequest(AddBookingRequestDTO request) {
        if (request.resourceId() == null) {
            throw new IllegalArgumentException(INVALID_RESOURCE_ID_MESSAGE);
        }
        validateTime(request.startTime(), request.endTime());
    }

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
