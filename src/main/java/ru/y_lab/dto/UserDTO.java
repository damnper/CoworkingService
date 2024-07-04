package ru.y_lab.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends BaseDTO{
    private Long id;
    private String username;
    private String password;
    private String role;
}