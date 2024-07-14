package ru.y_lab.exception;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }
}
