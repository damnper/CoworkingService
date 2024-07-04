package ru.y_lab.dto;

import lombok.Data;

@Data
public class ResourceDTO {
    private Long id;
    private Long userId;
    private String name;
    private String type;
}
