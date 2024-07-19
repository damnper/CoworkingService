package ru.y_lab.exception;

/**
 * Exception thrown when authorization fails.
 * This exception is typically used to indicate that a user does not have the necessary
 * permissions to access a resource or perform an action.
 */
public class AuthorizationException extends RuntimeException {

    /**
     * Constructs a new {@code AuthorizationException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public AuthorizationException(String message) {
        super(message);
    }
}
