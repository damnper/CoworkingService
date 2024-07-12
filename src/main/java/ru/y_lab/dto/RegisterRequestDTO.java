package ru.y_lab.dto;

/**
 * RegisterRequestDTO is a Data Transfer Object for registering a new user.
 *
 * @param username the username of the new user
 * @param password the password of the new user
 */
public record RegisterRequestDTO(String username, String password) { }
