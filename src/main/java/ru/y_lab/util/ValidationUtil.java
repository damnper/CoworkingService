package ru.y_lab.util;

import ru.y_lab.dto.RegisterRequestDTO;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Utility class for handling data validation.
 */
public class ValidationUtil {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,15}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9@#%]{6,20}$");
    private static final String INVALID_USERNAME_MESSAGE = "Invalid username. Username must be 3 to 15 characters long and can only contain letters, numbers, and underscores.";
    private static final String INVALID_PASSWORD_MESSAGE = "Invalid password. Password must be 6 to 20 characters long and can only contain letters, numbers, and the special characters @, #, and %.";

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
        if (!validateUsername(request.username()))
            throw new IllegalArgumentException(INVALID_USERNAME_MESSAGE);
        if (!validatePassword(request.password()))
            throw new IllegalArgumentException(INVALID_PASSWORD_MESSAGE);
    }

    public static void validateDateTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {

        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start time and end time must be provided.");
        }
        if (!startDateTime.isBefore(endDateTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
    }
}
