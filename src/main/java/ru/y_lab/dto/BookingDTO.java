package ru.y_lab.dto;


public record BookingDTO(Long id, Long userId, Long resourceId, String startTime, String endTime) {

}