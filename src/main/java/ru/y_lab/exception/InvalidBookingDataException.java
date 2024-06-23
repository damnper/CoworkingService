package ru.y_lab.exception;

/**
 * Exception thrown when invalid data is encountered during booking processing.
 */
public class InvalidBookingDataException extends RuntimeException {

    /**
     * Constructs a new InvalidBookingDataException with the specified detail message.
     * @param message the detail message
     */
    public InvalidBookingDataException(String message) {
        super(message);
    }
}
