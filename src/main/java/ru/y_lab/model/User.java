package ru.y_lab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user with details such as ID, username, password, and role.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long id;

    private String username;

    private String password;

    private String role;
}

