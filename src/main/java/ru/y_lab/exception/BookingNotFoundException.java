package ru.y_lab.exception;

/**
 * Exception thrown when a booking is not found.
 */
public class BookingNotFoundException extends RuntimeException {

    /**
     * Constructs a new BookingNotFoundException with the specified detail message.
     * @param message the detail message
     */
    public BookingNotFoundException(String message) {
        super(message);
    }
}
