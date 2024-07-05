package ru.y_lab.dto;


import java.time.LocalDateTime;

public record BookingDTO(Long id, Long userId, Long resourceId, LocalDateTime startTime, LocalDateTime endTime) { }