package ru.y_lab.dto;

/**
 * ResourceWithOwnerDTO is a Data Transfer Object that represents a resource along with its owner details.
 *
 * @param id the unique identifier of the resource
 * @param resourceName the resourceName of the resource
 * @param resourceType the type of the resource
 * @param userId the unique identifier of the user who owns the resource
 * @param ownerName the username of the user who owns the resource
 */
public record ResourceWithOwnerDTO(Long id,
                                   String resourceName,
                                   String resourceType,
                                   Long userId,
                                   String ownerName) { }
