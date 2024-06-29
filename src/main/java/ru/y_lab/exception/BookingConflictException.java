package ru.y_lab.exception;

/**
 * Exception thrown when there is a conflict with an existing booking.
 */
public class BookingConflictException extends Exception {

    /**
     * Constructs a new BookingConflictException with the specified detail message.
     * @param message the detail message
     */
    public BookingConflictException(String message) {
        super(message);
    }
}
