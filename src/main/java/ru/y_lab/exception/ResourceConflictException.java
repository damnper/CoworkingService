package ru.y_lab.exception;

public class ResourceConflictException extends Exception {

    /**
     * Constructs a new ResourceConflictException with the specified detail message.
     * @param message the detail message
     */
    public ResourceConflictException(String message) {
        super(message);
    }
}