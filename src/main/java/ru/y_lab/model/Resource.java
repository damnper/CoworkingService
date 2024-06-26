package ru.y_lab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a resource with details such as ID, user ID, name, and type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Resource {

    private String id;

    private String userId;

    private String name;

    private String type;
}


