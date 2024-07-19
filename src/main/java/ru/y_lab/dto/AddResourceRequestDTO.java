package ru.y_lab.dto;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AddResourceRequestDTO is a Data Transfer Object for adding a new resource.
 *
 * @param name the resourceName of the resource
 */
public record AddResourceRequestDTO(
        @Schema(name = "name", description = "Resource's name", example = "Resource name")
        String name) { }
