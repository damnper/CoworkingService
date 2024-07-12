package ru.y_lab.dto;

/**
 * UpdateUserRequestDTO is a Data Transfer Object for updating user details.
 *
 * @param username the new username of the user
 * @param password the new password of the user
 */
public record UpdateUserRequestDTO(String username, String password) { }
