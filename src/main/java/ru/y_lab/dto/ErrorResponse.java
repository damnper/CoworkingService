package ru.y_lab.dto;

public record ErrorResponse(int status, String message, long timestamp) {
}
