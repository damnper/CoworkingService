package ru.y_lab.dto;

public record ResourceWithOwnerDTO(Long id, String name, String type, Long userId, String username) {
}
