package ru.y_lab.dto;

/**
 * ResourceDTO is a Data Transfer Object that represents a resource.
 *
 * @param id the unique identifier of the resource
 * @param userId the unique identifier of the user who owns the resource
 * @param name the name of the resource
 * @param type the type of the resource
 */
public record ResourceDTO(Long id, Long userId, String name, String type) { }
