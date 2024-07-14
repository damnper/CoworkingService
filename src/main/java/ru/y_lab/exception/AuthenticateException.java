package ru.y_lab.exception;

/**
 * Exception thrown when authentication fails.
 * This exception is typically used to indicate problems with user authentication, such as invalid credentials.
 */
public class AuthenticateException extends RuntimeException {

    /**
     * Constructs a new {@code AuthenticateException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public AuthenticateException(String message) {
        super(message);
    }
}
