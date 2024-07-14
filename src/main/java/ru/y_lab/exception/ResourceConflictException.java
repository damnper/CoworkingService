package ru.y_lab.exception;

/**
 * Exception thrown when a conflict occurs with a resource.
 * This exception is typically used to indicate that a resource conflict has occurred,
 * such as trying to create a resource that already exists.
 */
public class ResourceConflictException extends RuntimeException {

    /**
     * Constructs a new ResourceConflictException with the specified detail message.
     * @param message the detail message
     */
    public ResourceConflictException(String message) {
        super(message);
    }
}