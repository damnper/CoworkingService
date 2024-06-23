package ru.y_lab.exception;

/**
 * Exception thrown when an invalid booking time is detected.
 */
public class InvalidBookingTimeException extends RuntimeException {

    /**
     * Constructs a new InvalidBookingTimeException with the specified detail message.
     * @param message the detail message
     */
    public InvalidBookingTimeException(String message) {
        super(message);
    }
}