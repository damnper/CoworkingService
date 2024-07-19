package ru.y_lab.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Enum representing different roles within the system.
 */
@Getter
@RequiredArgsConstructor
public enum RoleType {

    /**
     * Default user role.
     */
    USER("Default user"),

    /**
     * Administrator role with higher privileges.
     */
    ADMIN("Admin");

    private final String displayName;
}