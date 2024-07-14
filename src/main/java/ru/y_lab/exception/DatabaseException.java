package ru.y_lab.exception;

/**
 * Exception thrown when a database operation fails.
 * This exception is typically used to indicate problems with database access, such as connection failures or SQL errors.
 */
public class DatabaseException extends RuntimeException {

    /**
     * Constructs a new {@code DatabaseException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public DatabaseException(String message) {
        super(message);
    }
}
