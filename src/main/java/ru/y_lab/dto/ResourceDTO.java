package ru.y_lab.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceDTO extends BaseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String type;
}
