package ru.y_lab.util;

import ru.y_lab.dto.RegisterRequestDTO;

import java.util.regex.Pattern;

/**
 * Utility class for handling data validation.
 */
public class ValidationUtil {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,15}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9@#%]{6,20}$");

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
            throw new IllegalArgumentException("Invalid username");
        }
        if (!validatePassword(request.password())) {
            throw new IllegalArgumentException("Invalid password");
        }
    }
}
