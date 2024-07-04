package ru.y_lab.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateRequestDTO extends BaseDTO {
    private String username;
    private String password;
}
