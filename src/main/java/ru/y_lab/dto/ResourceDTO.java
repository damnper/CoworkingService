package ru.y_lab.dto;

/**
 * ResourceDTO is a Data Transfer Object that represents a resource.
 *
 * @param id the unique identifier of the resource
 * @param ownerId the unique identifier of the user who owns the resource
 * @param resourceName the resourceName of the resource
 * @param type the type of the resource
 */
public record ResourceDTO(Long id, Long ownerId, String resourceName, String type) { }
