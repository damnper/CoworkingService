package ru.y_lab.exception;

/**
 * Exception thrown when a resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}