package ru.y_lab.dto;

/**
 * ErrorResponse is a Data Transfer Object for error responses.
 *
 * @param status the HTTP status code of the error
 * @param message the error message
 * @param timestamp the timestamp of when the error occurred
 */
public record ErrorResponse(int status, String message, long timestamp) { }
