package ru.y_lab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponseDTO (

        @Schema(name = "token", description = "The bearer token", example = "")
        String token) {
}
