package ru.y_lab.dto;

/**
 * UpdateResourceRequestDTO is a Data Transfer Object for updating resource details.
 *
 * @param name the new resourceName of the resource
 */
public record UpdateResourceRequestDTO(String name) { }
