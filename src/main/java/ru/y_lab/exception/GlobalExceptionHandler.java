package ru.y_lab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.y_lab.dto.ErrorResponse;

/**
 * Global exception handler for handling various exceptions thrown by the application.
 * This class uses Spring's {@link ControllerAdvice} to handle exceptions globally
 * and return appropriate HTTP responses with error details.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link UserNotFoundException} and returns a NOT_FOUND response.
     *
     * @param ex the {@link UserNotFoundException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status NOT_FOUND
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link IllegalArgumentException} and returns a BAD_REQUEST response.
     *
     * @param ex the {@link IllegalArgumentException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status BAD_REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link BookingConflictException} and returns a CONFLICT response.
     *
     * @param ex the {@link BookingConflictException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status CONFLICT
     */
    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponse> handleBookingConflictException(BookingConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles {@link BookingNotFoundException} and returns a NOT_FOUND response.
     *
     * @param ex the {@link BookingNotFoundException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status NOT_FOUND
     */
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link InvalidBookingDataException} and returns a BAD_REQUEST response.
     *
     * @param ex the {@link InvalidBookingDataException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status BAD_REQUEST
     */
    @ExceptionHandler(InvalidBookingDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingDataException(InvalidBookingDataException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link InvalidBookingTimeException} and returns a BAD_REQUEST response.
     *
     * @param ex the {@link InvalidBookingTimeException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status BAD_REQUEST
     */
    @ExceptionHandler(InvalidBookingTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingTimeException(InvalidBookingTimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ResourceConflictException} and returns a CONFLICT response.
     *
     * @param ex the {@link ResourceConflictException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status CONFLICT
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles {@link ResourceNotFoundException} and returns a NOT_FOUND response.
     *
     * @param ex the {@link ResourceNotFoundException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status NOT_FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link DatabaseException} and returns an INTERNAL_SERVER_ERROR response.
     *
     * @param ex the {@link DatabaseException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@link SecurityException} and returns an UNAUTHORIZED response.
     *
     * @param ex the {@link SecurityException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status UNAUTHORIZED
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles {@link AuthenticateException} and returns an UNAUTHORIZED response.
     *
     * @param ex the {@link AuthenticateException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status UNAUTHORIZED
     */
    @ExceptionHandler(AuthenticateException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticateException(AuthenticateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles {@link AuthorizationException} and returns a FORBIDDEN response.
     *
     * @param ex the {@link AuthorizationException} thrown
     * @return a {@link ResponseEntity} containing the error details and HTTP status FORBIDDEN
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
