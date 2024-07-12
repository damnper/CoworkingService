package ru.y_lab.dto;

/**
 * UserDTO is a Data Transfer Object that represents a user in the system.
 * It contains the user's ID, username, and role.
 *
 * @param id       the unique identifier of the user
 * @param username the username of the user
 * @param role     the role of the user (e.g., USER, ADMIN)
 */
public record UserDTO(Long id, String username, String role) { }