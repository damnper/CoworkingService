package ru.y_lab.dto;

/**
 * LoginRequestDTO is a Data Transfer Object for logging in a user.
 *
 * @param username the username of the user
 * @param password the password of the user
 */
public record LoginRequestDTO(String username, String password) { }